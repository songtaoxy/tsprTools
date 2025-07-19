#!/opt/homebrew/bin/bash

# set -e

# #################################
# 计时器, 开始
# #################################
source  "${HOME}/stsh/essential/essential.sh"
# source  "${HOME}/stsh/psswitch/stop_process"

# line_len=50
line_len=68
left_len=30
duble_line=$(formater -l "$line_len" -cs =)
single_line=$(formater -l "$line_len" -cs -)
dot=$(formater -l "$line_len" -cs -)

# log "$(formater -l ${line_len} -cs '-')"


# 在开头添加颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
NC='\033[0m' # No Color



# 函数：记录文件列表及行数变化
log_file_changes() {
    local log_file=$1
    local repo_dir=$2  # 显式传入 Git 仓库目录
    local base_ref=$3  # 基准引用（如 HEAD^）
    local target_ref=$4  # 目标引用（如 HEAD）

    # 调试信息：打印当前目录和仓库目录
    echo                            >> "$log_file"
    echo "[DEBUG] 当前目录: $(pwd)" >> "$log_file"
    echo "[DEBUG] 切换到仓库目录: $repo_dir" >> "$log_file"

    # 确保进入仓库目录
    cd "$repo_dir" || { echo "[ERROR] 无法进入目录: $repo_dir" >> "$log_file"; return 1; }

    # 检查当前目录是否是 Git 仓库
    if ! git rev-parse --git-dir >/dev/null 2>&1; then
        echo "[ERROR] 当前目录不是 Git 仓库: $(pwd)" >> "$log_file"
        return 1
    fi

    # 检查仓库是否有提交历史
    if ! git rev-list -n 1 --all >/dev/null 2>&1; then
        echo "[ERROR] 仓库没有提交历史" >> "$log_file"
        return 1
    fi

    # 检查 HEAD 引用是否有效
    if ! git rev-parse --verify "HEAD" >/dev/null 2>&1; then
        echo "[ERROR] HEAD 引用无效" >> "$log_file"
        return 1
    fi

    # 检查基准引用和目标引用是否有效
    if ! git rev-parse --verify "$base_ref" >/dev/null 2>&1; then
        echo "[WARNING] 基准引用 '$base_ref' 无效，使用默认引用 'HEAD'。" >> "$log_file"
        base_ref="HEAD"
    fi
    if ! git rev-parse --verify "$target_ref" >/dev/null 2>&1; then
        echo "[WARNING] 目标引用 '$target_ref' 无效，使用默认引用 'HEAD'。" >> "$log_file"
        target_ref="HEAD"
    fi

    # 如果基准引用和目标引用相同，跳过文件统计
    if [[ "$base_ref" == "$target_ref" ]]; then
        echo "[INFO] 基准引用和目标引用相同，跳过文件统计。" >> "$log_file"
        return 0
    fi

    # 记录本次操作涉及的文件列表及修改统计
    local modified_files=$(git diff --name-only "$base_ref" "$target_ref" | grep -vE '^/tmp/|^/var/')
    local added_files=$(git diff --name-only --diff-filter=A "$base_ref" "$target_ref" | grep -vE '^/tmp/|^/var/')
    local deleted_files=$(git diff --name-only --diff-filter=D "$base_ref" "$target_ref" | grep -vE '^/tmp/|^/var/')
    local renamed_files=$(git diff --name-only --diff-filter=R "$base_ref" "$target_ref" | grep -vE '^/tmp/|^/var/')

    # echo "[INFO] 本次操作涉及的文件列表及修改统计：" >> "$log_file"
    # echo "修改的文件：" >> "$log_file"
    # echo "$modified_files" >> "$log_file"
    # echo "新增的文件：" >> "$log_file"
    # echo "$added_files" >> "$log_file"
    # echo "删除的文件：" >> "$log_file"
    # echo "$deleted_files" >> "$log_file"
    # echo "重命名的文件：" >> "$log_file"
    # echo "$renamed_files" >> "$log_file"

    # 统计每个文件的操作行数变化，并添加表头
    echo "[INFO] 文件列表、类型、及操作行数：" >> "$log_file"
    printf "%-10s %-10s %-10s %s\n" "新增行数" "删除行数" "文件类型" "文件路径" >> "$log_file"

    # 统计修改的文件
    if [[ -n "$modified_files" ]]; then
        git diff --numstat "$base_ref" "$target_ref" | grep -vE '^/tmp/|^/var/' | awk '{printf "%-10s %-10s %-10s %s\n", $1, $2, "修改", $3}' >> "$log_file"
    fi

    # 统计新增的文件
    if [[ -n "$added_files" ]]; then
        git diff --numstat "$base_ref" "$target_ref" --diff-filter=A | grep -vE '^/tmp/|^/var/' | awk '{printf "%-10s %-10s %-10s %s\n", $1, $2, "新增", $3}' >> "$log_file"
    fi

    # 统计删除的文件
    if [[ -n "$deleted_files" ]]; then
        git diff --numstat "$base_ref" "$target_ref" --diff-filter=D | grep -vE '^/tmp/|^/var/' | awk '{printf "%-10s %-10s %-10s %s\n", $1, $2, "删除", $3}' >> "$log_file"
    fi

    # 统计重命名的文件
    if [[ -n "$renamed_files" ]]; then
        git diff --numstat "$base_ref" "$target_ref" --diff-filter=R | grep -vE '^/tmp/|^/var/' | awk '{printf "%-10s %-10s %-10s %s\n", $1, $2, "重命名", $3}' >> "$log_file"
    fi

    # 如果文件列表为空，提示用户
    if [[ -z "$modified_files" && -z "$added_files" && -z "$deleted_files" && -z "$renamed_files" ]]; then
        echo "[INFO] 本次操作未涉及文件变化。" >> "$log_file"
    fi
}

# 函数：检查未跟踪的文件
check_untracked_files() {
    local repo_dir=$1
    local log_file=$2

    cd "$repo_dir" || { echo "无法进入目录: $repo_dir" >> "$log_file"; return 1; }

    # 检查仓库是否有提交历史
    if ! git rev-parse --verify "HEAD" >/dev/null 2>&1; then
        echo "[ERROR] HEAD 引用无效，仓库可能没有提交历史。" >> "$log_file"
        return 1
    fi

    local untracked_files=$(git status --porcelain | grep '^??' | cut -c4- | grep -vE '^/tmp/|^/var/')
    if [[ -n "$untracked_files" ]]; then

        echo                             >> "$log_file"
        echo "[INFO] 发现未跟踪的文件：" >> "$log_file"
        echo "$untracked_files" >> "$log_file"

        # 检查是否有文件不在 .gitignore 中
        local files_to_add=""
        for file in $untracked_files; do
            if ! git check-ignore -q "$file"; then
                files_to_add+="$file "
            fi
        done

        if [[ -n "$files_to_add" ]]; then
            echo "[INFO] 以下文件未在 .gitignore 中忽略，正在添加到 Git 追踪：" >> "$log_file"
            echo "$files_to_add" >> "$log_file"
            git add $files_to_add >> "$log_file" 2>&1
            git commit -m "Auto commit: 添加未跟踪的文件" >> "$log_file" 2>&1

            # 调用文件统计函数
            log_file_changes "$log_file" "$repo_dir" "HEAD^" "HEAD"
        else
            echo "[INFO] 所有未跟踪的文件均在 .gitignore 中忽略，无需处理。" >> "$log_file"
        fi
    fi
}

# 函数：检查已跟踪但未提交的文件
check_uncommitted_files() {
    local repo_dir=$1
    local log_file=$2

    cd "$repo_dir" || { echo "无法进入目录: $repo_dir" >> "$log_file"; return 1; }

    # 检查仓库是否有提交历史
    if ! git rev-parse --verify "HEAD" >/dev/null 2>&1; then
        echo "[ERROR] HEAD 引用无效，仓库可能没有提交历史。" >> "$log_file"
        return 1
    fi

    local uncommitted_files=$(git status --porcelain | grep -v '^??' | cut -c4- | grep -vE '^/tmp/|^/var/')
    if [[ -n "$uncommitted_files" ]]; then
        echo                             >> "$log_file"
        echo "[INFO] 发现已跟踪但未提交的文件：" >> "$log_file"
        echo "$uncommitted_files" >> "$log_file"

        # 先 fetch 远程更新
        git fetch origin >> "$log_file" 2>&1

        # 尝试 merge
        local current_branch=$(git rev-parse --abbrev-ref HEAD)
        git merge "origin/$current_branch" >> "$log_file" 2>&1

        if [[ $? -ne 0 ]]; then
            echo "[ERROR] 合并冲突，请手动解决冲突。" >> "$log_file"
            echo "冲突文件：" >> "$log_file"
            git diff --name-only --diff-filter=U >> "$log_file"
            return 1
        else
            echo "[INFO] 合并成功。" >> "$log_file"
            git add . >> "$log_file" 2>&1
            git commit -m "Auto commit: 合并远程更新并提交本地更改" >> "$log_file" 2>&1
            log_file_changes "$log_file" "$repo_dir" "HEAD^" "HEAD"
            git push origin HEAD >> "$log_file" 2>&1

            if [[ $? -ne 0 ]]; then
                echo "[ERROR] 推送失败。" >> "$log_file"
                return 1
            else
                echo "[INFO] 推送成功。" >> "$log_file"
                return 0
            fi
        fi
    fi

    # 调用文件统计函数
    # log_file_changes "$log_file" "$repo_dir" "HEAD^" "HEAD"
}

# 函数：检查本地与远程分支的状态
check_branch_status() {
    local repo_dir=$1
    local log_file=$2

    cd "$repo_dir" || { echo "无法进入目录: $repo_dir" >> "$log_file"; return 1; }

    # 检查仓库是否有提交历史
    if ! git rev-parse --verify "HEAD" >/dev/null 2>&1; then
        echo "[ERROR] HEAD 引用无效，仓库可能没有提交历史。" >> "$log_file"
        return 1
    fi

    # 获取本地当前分支
    local local_branch=$(git rev-parse --abbrev-ref HEAD)

    # 获取本地、远程和基准提交的哈希值
    git fetch origin >> "$log_file" 2>&1
    local local_hash=$(git rev-parse "$local_branch")
    local remote_hash=$(git rev-parse "origin/$local_branch" 2>/dev/null)
    local base_hash=$(git merge-base "$local_branch" "origin/$local_branch" 2>/dev/null)

    # 如果远程分支不存在，跳过文件统计
    if [[ -z "$remote_hash" ]]; then
        echo "[WARNING] 远程分支 'origin/$local_branch' 不存在，跳过文件统计。" >> "$log_file"
        return 3
    fi

    # 调用文件统计函数
    log_file_changes "$log_file" "$repo_dir" "$local_hash" "$remote_hash"

    if [[ "$local_hash" == "$remote_hash" ]]; then
        echo "[INFO] 本地与远程分支一致。" >> "$log_file"
        return 0
    elif [[ "$local_hash" == "$base_hash" ]]; then
        echo "[INFO] 本地分支落后于远程分支。" >> "$log_file"
        return 1
    elif [[ "$remote_hash" == "$base_hash" ]]; then
        echo "[INFO] 本地分支领先于远程分支。" >> "$log_file"
        return 2
    else
        echo "[INFO] 本地与远程分支已分叉。" >> "$log_file"
        echo "[WARNING] 请手动处理分叉问题。" >> "$log_file"
        return 3
    fi
}

# 函数：处理远程有更新，本地无提交的情况
handle_remote_update() {
    local repo_dir=$1
    local log_file=$2

    cd "$repo_dir" || { echo "无法进入目录: $repo_dir" >> "$log_file"; return 1; }

    # 检查仓库是否有提交历史
    if ! git rev-parse --verify "HEAD" >/dev/null 2>&1; then
        echo "[ERROR] HEAD 引用无效，仓库可能没有提交历史。" >> "$log_file"
        return 1
    fi

    # 获取当前分支
    local current_branch=$(git rev-parse --abbrev-ref HEAD)

    # 执行 fetch 和 merge
    git fetch origin "$current_branch" >> "$log_file" 2>&1
    git merge origin/"$current_branch" >> "$log_file" 2>&1

    if [[ $? -ne 0 ]]; then
        echo "合并冲突，请手动解决冲突。" >> "$log_file"
        echo "冲突文件：" >> "$log_file"
        git diff --name-only --diff-filter=U >> "$log_file"
        return 1
    else
        echo "合并成功。" >> "$log_file"

        # 调用文件统计函数
        log_file_changes "$log_file" "$repo_dir" "HEAD^" "HEAD"

        return 0
    fi
}

# 函数：处理本地有提交，远程无更新的情况
handle_local_commit() {
    local repo_dir=$1
    local log_file=$2

    cd "$repo_dir" || { echo "无法进入目录: $repo_dir" >> "$log_file"; return 1; }

    # 检查仓库是否有提交历史
    if ! git rev-parse --verify "HEAD" >/dev/null 2>&1; then
        echo "[ERROR] HEAD 引用无效，仓库可能没有提交历史。" >> "$log_file"
        return 1
    fi

    # 添加、提交并推送
    git add . >> "$log_file" 2>&1
    git commit -m "Auto commit by script" >> "$log_file" 2>&1
    git push origin HEAD >> "$log_file" 2>&1

    if [[ $? -ne 0 ]]; then
        echo "推送失败。" >> "$log_file"
        return 1
    else
        echo "推送成功。" >> "$log_file"

        # 调用文件统计函数
        log_file_changes "$log_file" "$repo_dir" "HEAD^" "HEAD"

        return 0
    fi
}

# 函数：处理本地有提交，远程有更新的情况
handle_both_updates() {
    local repo_dir=$1
    local log_file=$2

    cd "$repo_dir" || { echo "无法进入目录: $repo_dir" >> "$log_file"; return 1; }

    # 检查仓库是否有提交历史
    if ! git rev-parse --verify "HEAD" >/dev/null 2>&1; then
        echo "[ERROR] HEAD 引用无效，仓库可能没有提交历史。" >> "$log_file"
        return 1
    fi

    # 获取当前分支
    local current_branch=$(git rev-parse --abbrev-ref HEAD)

    # 执行 fetch 和 merge
    git fetch origin "$current_branch" >> "$log_file" 2>&1
    git merge origin/"$current_branch" >> "$log_file" 2>&1

    if [[ $? -ne 0 ]]; then
        echo "合并冲突，请手动解决冲突。" >> "$log_file"
        echo "冲突文件：" >> "$log_file"
        git diff --name-only --diff-filter=U >> "$log_file"
        return 1
    else
        echo "合并成功。" >> "$log_file"

        # 调用文件统计函数
        log_file_changes "$log_file" "$repo_dir" "HEAD^" "HEAD"

        git push origin HEAD >> "$log_file" 2>&1
        if [[ $? -ne 0 ]]; then
            echo "推送失败。" >> "$log_file"
            return 1
        else
            echo "推送成功。" >> "$log_file"
            return 0
        fi
    fi
}

# 函数：处理单个仓库
process_repo() {
    local repo_name=$1
    local repo_url=$2
    local base_dir=$3
    local log_file=$4

    echo "repository name: $repo_name" >> "$log_file"
    echo "repository url: ${repo_url}" >> "$log_file"
    echo "repository home: ${base_dir}${repo_name}" >> "$log_file"
    echo "pid of handling the repository: $$">> "$log_file"
    echo "repository temp log path: $log_file" >> "$log_file"
    echo                                       >> "$log_file"


    # echo "开始处理仓库: $repo_name" >> "$log_file"
    local start_time=$(date +%s)

    # local repo_dir="$base_dir/$repo_name"
    local repo_dir="${base_dir}${repo_name}"

    # 判断仓库是否存在
    if [[ ! -d "$repo_dir" || ! -d "$repo_dir/.git" ]]; then
        echo "仓库不存在，正在克隆..." >> "$log_file"
        git clone "$repo_url" "$repo_dir" >> "$log_file" 2>&1
        if [[ $? -ne 0 ]]; then
            echo "克隆失败。" >> "$log_file"
            return 1
        else
            echo "克隆成功。" >> "$log_file"
            return 0
        fi
    fi

    # 检查未跟踪的文件

    # echo                    >> "$log_file"
    # echo "场景: 未跟踪文件" >> "$log_file"
    check_untracked_files "$repo_dir" "$log_file"

    # 检查已跟踪但未提交的文件
    # echo                    >> "$log_file"
    # echo "场景: 已跟踪但未提交" >> "$log_file"
    check_uncommitted_files "$repo_dir" "$log_file"

    # 检查分支状态
    # echo                    >> "$log_file"
    # echo "场景: 分析状态" >> "$log_file"
    check_branch_status "$repo_dir" "$log_file"
    local status=$?

    echo                    >> "$log_file"
    case $status in
        0)
            echo "场景: 本地与远程分支一致，无需操作。" >> "$log_file"
            ;;
        1)
            echo "场景: 本地分支落后于远程分支，正在更新..." >> "$log_file"
            handle_remote_update "$repo_dir" "$log_file"
            ;;
        2)
            echo "场景: 本地分支领先于远程分支，正在推送..." >> "$log_file"
            handle_local_commit "$repo_dir" "$log_file"
            ;;
        3)
            echo "场景: 本地与远程分支已分叉，正在合并..." >> "$log_file"
            handle_both_updates "$repo_dir" "$log_file"
            ;;
        *)
            echo "场景: 未知状态。" >> "$log_file"
            return 1
            ;;
    esac

    local end_time=$(date +%s)
    local duration=$((end_time - start_time))
    echo                                          >> "$log_file"
    echo "处理仓库 $repo_name 耗时: $duration 秒" >> "$log_file"
}

declare -a PIDS

# 主函数：处理关联数组中的仓库
process_repos() {
    declare -n repos=$1  # 关联数组的引用
    local base_dir=$2

    # 创建临时文件用于存储主线程日志
    local main_log=$(mktemp)
    echo                >> "$main_log"
    echo "主线程日志：" >> "$main_log"
    # echo "----------------------------------------" >> "$main_log"
    # log -l info $duble_line >> "$main_log"
    log -l info $single_line >> "$main_log"

    # 记录总开始时间
    local total_start_time=$(date +%s)

    # 并行处理每个仓库
    PIDS+=("主线程的进程: $$")
    for repo_name in "${!repos[@]}"; do
        local repo_url="${repos[$repo_name]}"
        local log_file=$(mktemp)  # 创建临时文件用于存储当前仓库的日志

        {
            # echo "开始处理仓库: $repo_name" >> "$log_file"
            process_repo "$repo_name" "$repo_url" "$base_dir" "$log_file"
            # echo "结束处理仓库: $repo_name" >> "$log_file"
        } &
        local repo_pid=$!

        PIDS+=("处理仓库${repo_name}的进程: $repo_pid")

        # 将日志文件路径保存到主日志中
        # echo "处理仓库${repo_name}的进程: $repo_pid" >> "$main_log"
        echo "仓库${repo_name}临时日志文件: $log_file" >> "$main_log"
    done

    # 等待所有并行任务完成
    wait

    echo  >> "$main_log"
    for pid in "${PIDS[@]}"; do
        echo "${pid}" >> "$main_log"
        # printf "%-10s %s %-s\n" ""     ":"   "${pid}" >> "$main_log"
    done
    echo  >> "$main_log"

    # 记录总结束时间
    local total_end_time=$(date +%s)
    local total_duration=$((total_end_time - total_start_time))
    echo "所有仓库处理总耗时: $total_duration 秒" >> "$main_log"
    # echo "----------------------------------------" >> "$main_log"
    log -l info $single_line >> "$main_log"

    # 汇总所有日志
    # echo              >> "$main_log"
    # echo "汇总日志：" >> "$main_log"
    # echo "----------------------------------------" >> "$main_log"
    for repo_name in "${!repos[@]}"; do
        local log_file=$(grep "仓库${repo_name}临时日志文件" "$main_log" | awk '{print $NF}')
        echo              >> "$main_log"
        echo              >> "$main_log"
        echo "汇总仓库日志(${repo_name})" >> "$main_log"
        # echo "----------------------------------------" >> "$main_log"
        log -l info $single_line >> "$main_log"
        cat "$log_file" >> "$main_log"
        rm -f "$log_file"  # 删除临时文件
    done

    # 输出主日志
    cat "$main_log"
    rm -f "$main_log"  # 删除临时文件
}



# stop obsidian 
echo "同步文件前关闭相关程序,防止文件占用导致同步失败!"
${HOME}/stsh/psswitch/stop_process obsidian
echo 
echo 

# 示例调用
cost_start=$(date +%s)
t_start=$(date '+%Y-%m-%d %H:%M:%S')


# 主仓库
# /Users/songtao/idcp/
# base_dir="$HOME/idcp2/"
base_dir="$HOME/idcp/"
# /Users/songtao/idcp/TSPRVD-IDCP
base_repos_dir="TSPRVD-IDCP"
declare -A base_repos
base_repos["$base_repos_dir"]="git@github.com:songtaoxy/TSPRVD-IDCP.git"


# 各个子仓库
# /Users/songtao/idcp/TSPRVD-IDCP/Contents/
sub_repos_dir="${base_dir}${base_repos_dir}/Contents/"
declare -A sub_repos
sub_repos["TSPRVD"]="git@github.com:songtaoxy/TSPRVD.git"
sub_repos["tsprvd-ext"]="git@github.com:songtaoxy/tsprvd-ext.git"
sub_repos["tsprvd-mobile"]="git@github.com:songtaoxy/tsprvd-mobile.git"
sub_repos["omniAssets"]="git@github.com:songtaoxy/omniAssets.git"
sub_repos["os"]="git@github.com:songtaoxy/os.git"
sub_repos["vim"]="git@github.com:songtaoxy/vim.git"
#sub_repos["vim"]="git@github.com:songtaoxy/vim.git"
sub_repos["yonyou"]="git@github.com:songtaoxy/yonyou.git"
sub_repos["yftc"]="git@github.com:songtaoxy/yftc.git"
sub_repos["btsp"]="git@github.com:songtaoxy/btsp.git"
# sub_repos["books-reading"]="git@github.com:songtaoxy/books-reading.git"
sub_repos["Java-manual-bravo1988"]="git@github.com:songtaoxy/Java-manual-bravo1988.git"


log -l info $duble_line
# log "$(formater -l ${line_len} -cs '-')" 
printf "%-21s %s %-s\n" "Repository List" ": " "Base Repository && All Domains Repository."
log -l info $duble_line
printf "%-21s %s %-s\n" "${base_repos_dir}" ": " "${base_repos[$base_repos_dir]}"
log "$(formater -l ${line_len} -cs '.')" 
for k in ${!sub_repos[@]}; do
  # k_format_1=$(echo $k | sed 's/.\(.*\)/\1/' | sed 's/\(.*\)./\1/')
  printf "%-21s %s %-s\n" "${k}" ": " "${sub_repos[$k]}"
done
# log "$(formater -l ${line_len} -cs '-')" 




echo
echo
log -l info $duble_line
echo "一, 处理主仓库"
log -l info $duble_line
process_repos base_repos "$base_dir"


echo
echo
log -l info $duble_line
echo "二, 处理子仓库"
log -l info $duble_line
process_repos sub_repos "$sub_repos_dir"



# --------------------------------------
# statics: cost
# --------------------------------------
cost_end=$(date +%s)
cost=$((${cost_end}-${cost_start}))
t_end=$(date '+%Y-%m-%d %H:%M:%S')
log "$(formater -l ${line_len} -cs '.')" 
echo 
log "$(formater -l ${line_len} -cl "cost" -cr "${cost} seconds")" 
log "$(formater -l ${line_len} -cl "Start at" -cr "${t_start}")" 
log "$(formater -l ${line_len} -cl "End at" -cr "${t_end}")" 

# --------------------------------------
# statics: end flag 
# --------------------------------------
echo
duble_line=$(formater -l "$line_len" -cs =)
log -l info $duble_line

# #################################
# 操作完之后, 启动 obsidian 
# #################################
${HOME}/stsh/psswitch/start obsidian
