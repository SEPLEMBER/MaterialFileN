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
            // Формируем читаемый отчет об ошибке (используем нативный метод Kotlin вместо android.util.Log)
            val stackTrace = e.stackTraceToString()
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
            // Намеренно оставляем пустым, чтобы избежать любой записи в logcat
            // в соответствии с требованиями безопасности оболочки.
        } finally {
            // Передаем управление стандартному обработчику Android 
            // (он покажет системный диалог "Приложение остановлено" и завершит процесс)
            defaultHandler?.uncaughtException(t, e) ?: exitProcess(2)
        }
    }
}
