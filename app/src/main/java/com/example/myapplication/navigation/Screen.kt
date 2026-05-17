package com.example.myapplication.navigation

/**
 * 定义应用中所有页面的导航路由
 * 使用原生 Navigation Compose 进行页面跳转，而非通过控制组件显隐来"画出"页面
 */
enum class Screen(val route: String) {
    /** 主页 - 类似 Google Authenticator 的列表布局 */
    Home("home"),

    /** 搜索页 - 通过 Container Transform 从搜索栏展开到全屏 */
    Search("search"),

    /** 设置页 - 通过菜单按钮使用 Shared Axis 过渡动画跳转的原生二级页面 */
    Settings("settings"),

    /** 添加账户页 - 通过 FAB 使用 Container Transform 动画展开的页面 */
    AddAccount("add_account")
}