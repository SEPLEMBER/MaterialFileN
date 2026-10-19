package me.zhanghai.android.files.app

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import kotlin.system.exitProcess

object CrashHandler : Thread.UncaughtExceptionHandler {
    private var defaultHandler: Thread.UncaughtExceptionHandler? = null
    private lateinit var appContext: Context

    fun init(context: Context) {
        // Сохраняем оригинальный обработчик, чтобы система могла корректно завершить приложение
        defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        // Используем ApplicationContext, чтобы избежать утечек памяти
        appContext = context.applicationContext
        Thread.setDefaultUncaughtExceptionHandler(this)
        // Устанавливаем имя потока для удобства
        Thread.currentThread().name = "main"
    }

    override fun uncaughtException(t: Thread, e: Throwable) {
        try {
            // Формируем читаемый отчет об ошибке
            val stackTrace = android.util.Log.getStackTraceString(e)
            val crashReport = buildString {
                append("=== MATERIAL FILES CRASH REPORT ===\n")
                append("Thread: ${t.name}\n")
                append("Exception: ${e.javaClass.simpleName}\n")
                append("Message: ${e.message}\n")
                append("-----------------------------------\n")
                append(stackTrace)
            }

            // Копируем в буфер обмена
            val clipboard = appContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("MaterialFiles_CrashLog", crashReport)
            clipboard.setPrimaryClip(clip)

            // Показываем уведомление пользователю
            Toast.makeText(
                appContext,
                "Произошел сбой. Стек-трейс скопирован в буфер обмена!",
                Toast.LENGTH_LONG
            ).show()

        } catch (reportException: Exception) {
            // Если даже копирование в буфер упало, выводим это в logcat
            android.util.Log.e("CrashHandler", "Failed to handle crash", reportException)
        } finally {
            // Передаем управление стандартному обработчику Android 
            // (он покажет системный диалог "Приложение остановлено" и завершит процесс)
            defaultHandler?.uncaughtException(t, e) ?: exitProcess(2)
        }
    }
}
