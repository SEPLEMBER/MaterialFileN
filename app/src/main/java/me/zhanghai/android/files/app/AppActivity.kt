/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.files.app

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import me.zhanghai.android.files.theme.custom.CustomThemeHelper
import me.zhanghai.android.files.theme.night.NightModeHelper

abstract class AppActivity : AppCompatActivity() {
    private var isDelegateCreated = false

    override fun getDelegate(): AppCompatDelegate {
        val delegate = super.getDelegate()

        if (!isDelegateCreated) {
            isDelegateCreated = true
            NightModeHelper.apply(this)
        }
        return delegate
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        CustomThemeHelper.apply(this)

        super.onCreate(savedInstanceState)
        
        // Добавляем флаг защиты от скриншотов, записи экрана и отображения в меню недавних приложений
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
    }

    override fun onSupportNavigateUp(): Boolean {
        if (!super.onSupportNavigateUp()) {
            finish()
        }
        return true
    }
}
