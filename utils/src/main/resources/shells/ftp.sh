#!/bin/bash

REMOTE_HOST="127.0.0.1"
REMOTE_FTP_PORT=21
REMOTE_SFTP_PORT=22
REMOTE_USER="songtao"
REMOTE_PASS="styj822763"

REMOTE_DIR="/Users/songtao/Downloads/ftp/ftptest-remote"
ZIP_LIST=("ftptest.zip" "t3.zip")

LOCAL_DIR1="/Users/songtao/Downloads/ftp/ftptest-loca-down"
LOCAL_DIR2="/Users/songtao/Downloads/ftp/ftptest-loca-unzip"
LOCAL_DIR3="/Users/songtao/Downloads/ftp/ftptest-loca-bak"

TODAY=$(date +%Y-%m-%d-%H-%M-%S)

# 日志相关
LOG_DIR="/Users/songtao/Downloads/ftp/ftptest-local-log"
LOG_FILE="$LOG_DIR/fetch_zip_$TODAY.log"


function log() {
  echo "[$(date '+%Y-%m-%d %H:%M:%S')] $*" | tee -a "$LOG_FILE"
}

function error_exit() {
  log "错误: $*"
  exit 1
}


# ==========================
# 检测目录是否存在; 
# 若目录已存在 → 无操作
# 若目录不存在 → 自动递归创建
# 若创建失败（如权限问题）→ 打印错误并退出
# ==========================
function ensure_local_dirs() {
  for dir in "$LOCAL_DIR1" "$LOCAL_DIR2" "$LOCAL_DIR3"; do
    if [ ! -d "$dir" ]; then
      echo "目录 $dir 不存在，正在创建..."
      mkdir -p "$dir"
      if [ $? -ne 0 ]; then
        echo "创建目录 $dir 失败，请检查权限" >&2
        exit 1
      fi
    fi
  done
}

# ==========================
# 检测目录是否存在; 日志相关目录 
# 若目录已存在 → 无操作
# 若目录不存在 → 自动递归创建
# 若创建失败（如权限问题）→ 打印错误并退出
# ==========================
function ensure_log_dir() {
  if [ ! -d "$LOG_DIR" ]; then
    mkdir -p "$LOG_DIR" || {
      echo "日志目录创建失败: $LOG_DIR"
      exit 1
    }
  fi
}



# ==========================
# 自动切换脚本执行目录到 $LOCAL_DIR1
# cd 到 $LOCAL_DIR1，后续操作默认以此为起点
# 避免路径混乱，确保相对路径行为稳定
# ==========================
function switch_to_download_dir() {
  cd "$LOCAL_DIR1" || {
    echo "切换目录失败：$LOCAL_DIR1 不存在或无法访问"
    exit 1
  }
}

function ftp_download_file() {
  local FILE=$1
  log "FTP 下载 $FILE"
  ftp -inv $REMOTE_HOST <<EOF >> "$LOG_FILE" 2>&1
user $REMOTE_USER $REMOTE_PASS
cd $REMOTE_DIR
lcd $LOCAL_DIR1
get $FILE
bye
EOF
}

# -a 表示断点续传
function sftp_download_file() {
  local FILE=$1
  log "SFTP 下载 $FILE"
  sftp -P $REMOTE_SFTP_PORT $REMOTE_USER@$REMOTE_HOST <<EOF >> "$LOG_FILE" 2>&1
cd $REMOTE_DIR
lcd $LOCAL_DIR1
get -a $FILE
bye
EOF
}

#  -c 表示续传；-n 为并发线程数（视情况调整）
function lftp_download_file() {
  local FILE=$1
  log "lftp 下载 $FILE ,支持断点续传"
  lftp -u "$REMOTE_USER","$REMOTE_PASS" -p $REMOTE_SFTP_PORT $REMOTE_HOST <<EOF >> "$LOG_FILE" 2>&1
set ftp:ssl-allow no
cd $REMOTE_DIR
lcd $LOCAL_DIR1
pget -n 4 -c $FILE
bye
EOF
}


# ==========================
# 备份远程文件
# ==========================
function ftp_remote_backup() {
  local FILE=$1
  local NEW_NAME="${FILE}${BACKUP_SUFFIX}"

  log "FTP 远程备份 $FILE 为 $NEW_NAME"

  ftp -inv $REMOTE_HOST <<EOF >> "$LOG_FILE" 2>&1
user $REMOTE_USER $REMOTE_PASS
cd $REMOTE_DIR
rename $FILE $NEW_NAME
bye
EOF
}

function sftp_remote_backup() {
  local FILE=$1
  local NEW_NAME="${FILE}${BACKUP_SUFFIX}"

  log "SFTP 远程备份 $FILE 为 $NEW_NAME"

  sftp -P $REMOTE_SFTP_PORT $REMOTE_USER@$REMOTE_HOST <<EOF >> "$LOG_FILE" 2>&1
cd $REMOTE_DIR
rename $FILE $NEW_NAME
bye
EOF
}

function lftp_remote_backup() {
  local FILE=$1
  local NEW_NAME="${FILE}${BACKUP_SUFFIX}"

  log "lftp 远程备份 $FILE 为 $NEW_NAME"

  lftp -u "$REMOTE_USER","$REMOTE_PASS" -p $REMOTE_FTP_PORT $REMOTE_HOST <<EOF >> "$LOG_FILE" 2>&1
set ftp:ssl-allow no
set ftp:passive-mode on
cd $REMOTE_DIR
rename $FILE $NEW_NAME
bye
EOF
}


# ==========================
# 处理文件: 检测是否下载成功-》解压-》备份本地文件
# ==========================
function process_file() {
  local FILE=$1
  local BACKUP_NAME="${FILE%.zip}_$TODAY.bak"

  # 判断是否已成功下载
  if [ -f "$LOCAL_DIR1/$FILE" ]; then
    log "文件 $FILE 下载成功，开始解压"
    unzip -o "$LOCAL_DIR1/$FILE" -d "$LOCAL_DIR2" >> "$LOG_FILE" 2>&1 || {
      log "解压 $FILE 失败"
      return
    }

    log "备份 $FILE 为 $BACKUP_NAME"
    mv "$LOCAL_DIR1/$FILE" "$LOCAL_DIR3/$BACKUP_NAME" || {
      log "移动备份失败: $FILE"
    }
  else
    log "文件 $FILE 下载失败，跳过处理"
  fi
}

# ==========================
# 处理文件: 检测是否下载成功; 不成功, 则尝试多种方式下载; 下载成功后, 备份远程
# ==========================
function download_file_with_fallback() {
  local FILE=$1

  ftp_download_file "$FILE"
  if [ -f "$LOCAL_DIR1/$FILE" ]; then
    log "FTP 成功：$FILE"
    ftp_remote_backup "$FILE"
    return
  fi


   log "FTP 失败，尝试 SFTP"
    sftp_download_file "$FILE"
    if [ -f "$LOCAL_DIR1/$FILE" ]; then
      log "SFTP 成功：$FILE"
      sftp_remote_backup "$FILE"
      return
    fi


  log "SFTP 失败，尝试 LFTP"
  if command -v lftp >/dev/null 2>&1; then
    lftp_download_file "$FILE"
    if [ -f "$LOCAL_DIR1/$FILE" ]; then
      log "lftp 成功：$FILE"
      lftp_remote_backup "$FILE"
    else
      log "lftp 失败：$FILE"
    fi
  else
    log "SFTP 失败，lftp 不存在，放弃：$FILE"
  fi




}

# 
function main() {
  ensure_log_dir
  ensure_local_dirs
  switch_to_download_dir

  for ZIP in "${ZIP_LIST[@]}"; do
    log ">>>> 正在处理文件：$ZIP"
    download_file_with_fallback "$ZIP"
    process_file "$ZIP"
  done
  log "脚本执行完毕"

 # 其他功能可扩展
}


log "===== 批量下载开始 ====="
main
log "===== 所有文件处理完成 ====="

