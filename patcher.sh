#!/bin/bash
set -e

echo "=== ЗАПУСК ПАТЧЕРА СРЕДЫ MOTIONGRAM ==="

# 1. Скачиваем официальную чистую FOSS-базу Telegram без сервисов Google
if [ ! -d "telegram_source" ]; then
    echo "[1/4] Клонирование официального Telegram-FOSS..."
    git clone --depth=1 https://github.com telegram_source
else
    echo "[1/4] Папка исходников уже существует."
fi

# 2. Переносим наше санированное ядро в архитектуру Telegram
echo "[2/4] Инъекция модулей монолитного ядра..."
TARGET_DIR="telegram_source/TMessagesProj/src/main/java/org/telegram/ui/Components"
mkdir -p "$TARGET_DIR"

cp MotionJellyEngine.java "$TARGET_DIR/"
cp MotionCryptoEngine.java "$TARGET_DIR/"
cp MotionProxyManager.java "$TARGET_DIR/"
cp MotionTerminal.java "$TARGET_DIR/"
cp MotionOsintTab.java "$TARGET_DIR/"

# 3. Модифицируем UI Telegram (Врезка хакерской консоли и маскиратора)
echo "[3/4] Модификация оригинальных файлов интерфейса Telegram..."

CHAT_ACTIVITY="telegram_source/TMessagesProj/src/main/java/org/telegram/ui/ChatActivity.java"

if [ -f "$CHAT_ACTIVITY" ]; then
    # Находим точку отправки сообщения и врезаем туда перехватчик команд нашего терминала
    # Если команда распознана, она выведется на экран, а отправка в сеть заблокируется
    sed -i '/String message =/a \    String checkCmd = org.telegram.ui.Components.MotionTerminal.executeCommand(message);\n    if(!checkCmd.isEmpty()) { showAlert(checkCmd); return; }' "$CHAT_ACTIVITY"
    echo "-> Интеграция в ChatActivity.java выполнена успешно."
else
    echo "-> Ошибка: Файл ChatActivity.java не найден. Проверьте структуру репозитория."
    exit 1
fi

# 4. Настройка прав сборщика
echo "[4/4] Подготовка Gradle-окружения к финальной сборке APK..."
chmod +x telegram_source/gradlew

echo "=== МОДИФИКАЦИЯ БАЗЫ ТЕЛЕГРАМА ЗАВЕРШЕНА НА 100% ==="
