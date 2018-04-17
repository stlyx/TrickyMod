package io.github.stlyx.trickymod

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge.log
import de.robv.android.xposed.XposedHelpers.findAndHookConstructor
import de.robv.android.xposed.XposedHelpers.findAndHookMethod
import de.robv.android.xposed.callbacks.XC_LoadPackage
import io.github.stlyx.trickymod.Global.PREFERENCE_NAME_SETTINGS
import io.github.stlyx.trickymod.Global.PREF_SPEC_DEFAULT_NOTIFICATION_CATEGORY
import io.github.stlyx.trickymod.Global.tryWithLog
import io.github.stlyx.trickymod.utils.Preferences


class ModNotification : IXposedHookLoadPackage {

    private val settings = Preferences()

    // NOTE: Hooking Application.attach is necessary because Android 4.X is not supporting
    //       multi-dex applications natively. More information are available in this link:
    //       https://github.com/rovo89/xposedbridge/issues/30
    // NOTE: Since Wechat 6.5.16, the MultiDex installation became asynchronous. It is not
    //       guaranteed to be finished after Application.attach, but the exceptions caused
    //       by this can be ignored safely (See details in tryHook).
    private inline fun hookApplicationAttach(loader: ClassLoader, crossinline callback: (Context) -> Unit) {
        findAndHookMethod("android.app.Application", loader, "attach", C.Context, object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                callback(param.thisObject as Context)
            }
        })
    }

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        tryWithLog {
            hookApplicationAttach(lpparam.classLoader, { context ->
                hookNotification(lpparam, context)
            })
        }
    }

    private fun hookNotification(lpparam: XC_LoadPackage.LoadPackageParam, context: Context) {
        val except = arrayOf("me.zhanghai.android.wechatnotificationtweaks2")
        settings.listen(context)
        settings.init(PREFERENCE_NAME_SETTINGS)

        if (settings.getBoolean(PREF_SPEC_DEFAULT_NOTIFICATION_CATEGORY, true)) {
            if (except.contains(lpparam.packageName)) return

            findAndHookConstructor(C.NotificationBuilder, C.Context, object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam?) {
                    // log("stlyx: before returning Notification.Builder(Context)")
                    val nb = param?.thisObject as Notification.Builder
                    enhanceNotification(param.args[0] as Context, nb)
                    param.result = nb
                }
            })

            findAndHookConstructor(C.NotificationBuilder, C.Context, C.String, object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam?) {
                    if (param?.args?.get(1) != null) return
                    //log("stlyx: before returning Notification.Builder(Context, null)")
                    val nb = param?.thisObject as Notification.Builder
                    enhanceNotification(param.args[0] as Context, nb)
                    param.result = nb
                }
            })
        }
    }

    private fun enhanceNotification(ctx: Context, nb: Notification.Builder) {
        val mNotificationManager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (mNotificationManager.notificationChannels.none { it.id == "undefined" }) {
            mNotificationManager.createNotificationChannel(NotificationChannel(
                    "undefined",
                    "Undefined by TrickyMod",
                    NotificationManager.IMPORTANCE_LOW
            ))
        }
        nb.setChannelId("undefined")
    }
}
