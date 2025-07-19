# V20250719.02
feature: 错误时高亮显示, 给出提示, 并退出.

## 问题

无网或连接失败时, 日志已经输出ssh: connect to host github.com port 22: Undefined error: 0 fatal: Could not read from remote repository 此时, 应该仓库 $repo_name 处理失败，退出码并给出原因, 同时终止程序
## 其一

你提到的问题明确指出了一种常见场景：

> 当 `git clone` 或 `git fetch` 因网络问题或无法连接远程仓库时（例如 `ssh: connect to host github.com port 22: Undefined error: 0`），虽然日志中出现了错误信息，但由于没有显式处理错误码，这种情况可能仍被视为“处理成功”，这是**不正确的行为**。

### 解决方案目标

在以下场景中，明确设置返回码非 0，以便：

1. `process_repo` 能准确返回失败状态
2. `process_repos` 汇总时将其识别为失败
3. 全局终止程序

------

### 一、`git clone` 时的错误返回处理

已处理如下，无需修改：

```bash
git clone "$repo_url" "$repo_dir" >> "$log_file" 2>&1
if [[ $? -ne 0 ]]; then
    echo "克隆失败。" >> "$log_file"
    return 1
```

------

### 二、补充 `git fetch` 和 `git push` 失败时退出码

重点修改以下函数中的错误处理逻辑：

#### 1. `check_branch_status` → 确保 fetch 失败处理：

```bash
git fetch origin >> "$log_file" 2>&1
if [[ $? -ne 0 ]]; then
    echo "[ERROR] git fetch 失败，可能无法连接远程仓库。" >> "$log_file"
    return 1
fi
```

#### 2. `handle_remote_update`、`handle_both_updates`、`handle_local_commit`：

```bash
git fetch origin "$current_branch" >> "$log_file" 2>&1
if [[ $? -ne 0 ]]; then
    echo "[ERROR] fetch 失败，无法连接远程。" >> "$log_file"
    return 1
fi
git push origin HEAD >> "$log_file" 2>&1
if [[ $? -ne 0 ]]; then
    echo "[ERROR] 推送失败，可能无法连接远程仓库。" >> "$log_file"
    return 1
fi
```

------

### 三、统一处理：只要任何一步连接失败，函数退出码设为非 0

这样 `process_repo` 就能正确捕捉失败，然后：

```bash
echo "$repo_name:$?" >> "$status_file"
```

即可记录失败，主函数汇总判断也就会输出：

```bash
[ERROR] 仓库 $repo_name 处理失败，退出码: 1
[FATAL] 有仓库处理失败，终止后续操作。
```

------

### 四、最终效果

- 出现 `ssh: connect to host github.com ...` → 被捕捉进日志
- 脚本输出 `[ERROR] 仓库 xxx 处理失败，退出码: 1`
- 输出 `[FATAL] 有仓库处理失败，终止后续操作。`
- 退出码为 1，脚本终止

------

如需我提供完整改写后的文件，请确认是否保留其他日志结构与颜色输出等，再为你导出新版脚本。是否现在直接导出？

## 其二

根据你反馈的行为：即使 `git clone` 或 `git fetch` 等操作失败，`process_repo` 仍返回 0，导致汇总时错误地认为“仓库处理成功”。这是因为：

> 你当前脚本中的 `process_repo` 函数 **调用多个子函数**，但这些子函数如果失败，并没有中断 `process_repo` 的执行，最终 `process_repo` 没有明确的 `return`，默认返回上一条命令的状态码，而你可能最后执行的是 `echo`。

------

### 解决方案

我们要让 `process_repo` 准确返回错误码，并且只要中途任何步骤失败，就提前退出 `process_repo` 并 `return 1`。

------

### 一、修改 `process_repo` 函数

在每个函数调用后检查返回值，立即 `return 1`：

```bash
check_untracked_files "$repo_dir" "$log_file"
[[ $? -ne 0 ]] && return 1

check_uncommitted_files "$repo_dir" "$log_file"
[[ $? -ne 0 ]] && return 1

check_branch_status "$repo_dir" "$log_file"
local status=$?
[[ "$status" -gt 3 ]] && return 1  # 非法状态直接失败
```

在调用 `handle_xxx` 时也加判断：

```bash
case $status in
    0)
        echo "场景: 本地与远程分支一致，无需操作。" >> "$log_file"
        ;;
    1)
        echo "场景: 本地分支落后于远程分支，正在更新..." >> "$log_file"
        handle_remote_update "$repo_dir" "$log_file"
        [[ $? -ne 0 ]] && return 1
        ;;
    2)
        echo "场景: 本地分支领先于远程分支，正在推送..." >> "$log_file"
        handle_local_commit "$repo_dir" "$log_file"
        [[ $? -ne 0 ]] && return 1
        ;;
    3)
        echo "场景: 本地与远程分支已分叉，正在合并..." >> "$log_file"
        handle_both_updates "$repo_dir" "$log_file"
        [[ $? -ne 0 ]] && return 1
        ;;
    *)
        echo "场景: 未知状态。" >> "$log_file"
        return 1
        ;;
esac
```

同时，`git clone` 失败时已有 return 1，可保留：

```bash
git clone "$repo_url" "$repo_dir" >> "$log_file" 2>&1
if [[ $? -ne 0 ]]; then
    echo "克隆失败。" >> "$log_file"
    return 1
fi
```

------

### 二、为什么这种修改有效？

因为每次失败后 `process_repo` 会显式地 `return 1`，最终在 `process_repos` 中：

```bash
process_repo "$repo_name" "$repo_url" "$base_dir" "$log_file"
echo "$repo_name:$?" >> "$status_file"
```

就能捕获 `1` 而非错误的 `0`，从而在状态汇总中正确输出 `[ERROR]`。

------

### 三、建议增强的日志提示

你可以在每个 `[[ $? -ne 0 ]]` 后追加日志：

```bash
[[ $? -ne 0 ]] && echo "[ERROR] check_uncommitted_files 失败" >> "$log_file" && return 1
```

------

### 四、补充建议：`set -e` 与保留结构冲突

虽然加上 `set -e` 会立即终止，但你当前是并行执行多任务，用 `set -e` 会导致父进程中断所有子任务。建议依旧手动控制 `return`。

------

是否需要我直接为你更新并导出此增强版本？你可继续回复“是”。


# V20250719

## 优化说明

为提升你的脚本在出现错误时的可诊断性与鲁棒性，可采用以下改进策略：

### 一、核心目标

1. **在任何一个子进程出错时，主流程能够捕获并汇总错误**
2. **输出醒目提示（如 `[ERROR]` 红色或高亮）**
3. **可选：立即终止整个主流程，防止继续同步其他仓库**

------

### 二、修改点一：process_repo 中统一返回码

你已有完整的 `return 1` 语句逻辑，保留即可，不动。

------

### 三、修改点二：process_repos 中收集状态并判断

请在 `process_repos` 函数中的每个后台任务后添加如下逻辑：

```bash
# 记录每个仓库是否失败
local status_file=$(mktemp)
```

每个 repo 启动后台任务时，在 `process_repo` 后补充：

```bash
process_repo "$repo_name" "$repo_url" "$base_dir" "$log_file"
echo "$repo_name:$?" >> "$status_file"
```

------

### 四、修改点三：任务结束后统一检查失败仓库

在 `wait` 后追加如下逻辑：

```bash
local failed=0
echo >> "$main_log"
echo "任务执行状态汇总：" >> "$main_log"
while read -r line; do
    repo_name=$(echo "$line" | cut -d: -f1)
    exit_code=$(echo "$line" | cut -d: -f2)
    if [[ "$exit_code" -ne 0 ]]; then
        echo "[ERROR] 仓库 $repo_name 处理失败，退出码: $exit_code" >> "$main_log"
        failed=1
    else
        echo "[OK] 仓库 $repo_name 处理成功。" >> "$main_log"
    fi
done < "$status_file"
rm -f "$status_file"
```

如需立即中止主流程：

```bash
if [[ "$failed" -ne 0 ]]; then
    echo "[FATAL] 有仓库处理失败，终止后续操作。" >> "$main_log"
    cat "$main_log"
    rm -f "$main_log"
    exit 1
fi
```

------

### 五、可选：突出颜色提示（仅限 iTerm2/TTY 支持）

示例：

```bash
RED='\033[0;31m'
GREEN='\033[0;32m'
NC='\033[0m' # No Color

echo -e "${RED}[ERROR] 仓库 $repo_name 处理失败${NC}"
```

如在 GUI 工具或 log 文件中，建议保留 `[ERROR]` 明示。

------

### 六、最终效果

- 若任何仓库失败：会在汇总日志中标明 `[ERROR] 仓库 xxx 处理失败`
- 可配置是否：
  - 继续执行其他仓库（默认行为）
  - 立刻终止主流程（设置 `exit 1`）
- 用户更容易定位失败仓库和失败位置
- 日志结构清晰，便于复查与 CI 集成

------

如你需要，我可以帮你整合上述逻辑为完整的修改版本，是否需要？

## 新版内容

```bash
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
    echo                   "Summary"
    echo                >> "$main_log"
    echo "主线程日志：" >> "$main_log"
    # echo "----------------------------------------" >> "$main_log"
    # log -l info $duble_line >> "$main_log"
    log -l info $single_line >> "$main_log"

    # 记录总开始时间
    local total_start_time=$(date +%s)


    # 创建状态临时文件
    local status_file=$(mktemp)


    # 并行处理每个仓库
    PIDS+=("主线程的进程: $$")
    for repo_name in "${!repos[@]}"; do
        local repo_url="${repos[$repo_name]}"
        local log_file=$(mktemp)  # 创建临时文件用于存储当前仓库的日志

        {
            # echo "开始处理仓库: $repo_name" >> "$log_file"
            process_repo "$repo_name" "$repo_url" "$base_dir" "$log_file"
            # echo "结束处理仓库: $repo_name" >> "$log_file"
            echo "$repo_name:$?" >> "$status_file"

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

    # 状态汇总
    local failed=0
    echo >> "$main_log"
    echo "任务执行状态汇总：" >> "$main_log"
    while read -r line; do
        repo_name=$(echo "$line" | cut -d: -f1)
        exit_code=$(echo "$line" | cut -d: -f2)
        if [[ "$exit_code" -ne 0 ]]; then
            echo -e "${RED}[ERROR] 仓库 $repo_name 处理失败，退出码: $exit_code${NC}" | tee -a "$main_log"
            failed=1
        else
            echo -e "${GREEN}[OK] 仓库 $repo_name 处理成功。${NC}" | tee -a "$main_log"
        fi
    done < "$status_file"
    rm -f "$status_file"

    # 可选中止行为
    if [[ "$failed" -ne 0 ]]; then
        echo -e "${RED}[FATAL] 有仓库处理失败，终止后续操作。${NC}" | tee -a "$main_log"
        cat "$main_log"
        rm -f "$main_log"
        exit 1
    fi



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

```
## 原始内容

```bash
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

```