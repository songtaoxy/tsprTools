#!/usr/bin/env bash

# Usage
# -b 指定分支; 可以多个 -b 分支a -b 分支b
# -a 作者
# -s 开始时间
# -e 截止时间
# 具体, ref obsidian
# #################################
#./git4.sh -s 2024-11-25 -e 2025-01-17
#./git4.sh -a songtao
#./git4.sh -b master -s 2024-11-25 -e 2025-01-17 -a songtao
# #################################



# ============ 配置区 =============
TMP_ALL_CHANGES=$(mktemp)
TMP_DEL_FILES=$(mktemp)
TMP_CHANGED_FILES=$(mktemp)

# 默认全部分支
BRANCHES=""

# 参数解析
while [[ $# -gt 0 ]]; do
  case "$1" in
    -a|--author) AUTHOR="$2"; shift 2;;
    -b|--branch) BRANCHES="$BRANCHES $2"; shift 2;;
    -s|--since)  SINCE="$2"; shift 2;;
    -e|--until)  UNTIL="$2"; shift 2;;
    *) shift;;
  esac
done

if [[ -z "$BRANCHES" ]]; then
  BRANCHES=$(git for-each-ref --format='%(refname:short)' refs/heads/)
fi

echo -e "分支\t提交哈希\t作者\t时间\t操作\t文件"

for BRANCH in $BRANCHES
do
  git checkout "$BRANCH" >/dev/null 2>&1
  LOG_OPTS="--pretty=format:BRANCH:$BRANCH|HASH:%H|AUTHOR:%an|DATE:%ad --name-status --date=short --no-renames"
  [[ -n "$AUTHOR" ]] && LOG_OPTS="$LOG_OPTS --author=$AUTHOR"
  [[ -n "$SINCE"  ]] && LOG_OPTS="$LOG_OPTS --since=$SINCE"
  [[ -n "$UNTIL"  ]] && LOG_OPTS="$LOG_OPTS --until=$UNTIL"

  git log $LOG_OPTS | awk -v branch="$BRANCH" '
    BEGIN{hash="";author="";date=""}
    /^BRANCH:/{
      split($0, a, /\|/);
      branch=substr(a[1],8);
      hash=substr(a[2],6);
      author=substr(a[3],8);
      date=substr(a[4],6);
      next
    }
    /^[AMD]\t/{
      op=substr($0,1,1);
      file=substr($0,3);
      op2=(op=="A"?"新增":(op=="D"?"删除":"修改"));
      printf("%s\t%s\t%s\t%s\t%s\t%s\n", branch, hash, author, date, op2, file)
    }
  '
done | tee "$TMP_ALL_CHANGES"

# 记录所有发生变化的文件（新增/修改）
awk -F'\t' '$5=="新增"||$5=="修改"{print $6}' "$TMP_ALL_CHANGES" | sort | uniq > "$TMP_CHANGED_FILES"
# 记录所有被删除的文件
awk -F'\t' '$5=="删除"{print $6}' "$TMP_ALL_CHANGES" | sort | uniq > "$TMP_DEL_FILES"

# 切回原分支
INITIAL_BRANCH=$(git symbolic-ref --short HEAD 2>/dev/null)
if [ -n "$INITIAL_BRANCH" ]; then
  git checkout "$INITIAL_BRANCH" >/dev/null 2>&1
fi

# ======================= 输出分割 =======================

echo ""
echo "==================== 变化的文件清单（去重，含新增/更新, 附最新操作详情）=================="
awk -F'\t' '$5=="新增"||$5=="修改"{print $0}' "$TMP_ALL_CHANGES" \
| sort -k6,6 -k4,4r -k2,2r \
| awk -F'\t' '!a[$6]++' \
| awk -F'\t' '{printf("%-12s %-12s %-12s %-8s %-40s %s\n", $1, $3, $4, $5, $6, $2)}'

echo ""
echo "==================== 删除的文件清单（去重, 附最新操作详情）=================="
awk -F'\t' '$5=="删除"{print $0}' "$TMP_ALL_CHANGES" \
| sort -k6,6 -k4,4r -k2,2r \
| awk -F'\t' '!a[$6]++' \
| awk -F'\t' '{printf("%-12s %-12s %-12s %-8s %-40s %s\n", $1, $3, $4, $5, $6, $2)}'

# 清理临时文件
rm -f "$TMP_ALL_CHANGES" "$TMP_CHANGED_FILES" "$TMP_DEL_FILES"

echo ""
echo "统计完成。"