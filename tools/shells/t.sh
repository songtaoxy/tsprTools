#!/bin/bash

# ============================================
# 文件本身路径或目录
# ============================================

# result
# --------------------------------------------
#$ ./t.sh
#file_dir_abbr: .
#file_dir_full: /Users/songtao/personaldriveMac/Projects/tsprTools/tools/shells
#file_path_full: /Users/songtao/personaldriveMac/Projects/tsprTools/tools/shells/t.sh
#project_dir: /Users/songtao/personaldriveMac/Projects/tsprTools
# --------------------------------------------

# 文件本身dir
file_dir_abbr=$(dirname "$0")
file_dir_full=$(readlink -f "$file_dir_abbr")
echo "file_dir_abbr: $file_dir_abbr"
echo "file_dir_full: $file_dir_full"

# 文件本身绝对path
file_path_full=$(readlink -f "$0")
echo "file_path_full: $file_path_full"

# 文件所在的目录,该目录上两级, 即项目的目录
# project_dir=$(dirname $(dirname $(pwd)))
project_dir=$(dirname $(dirname $file_dir_full))
echo "project_dir: $project_dir"
