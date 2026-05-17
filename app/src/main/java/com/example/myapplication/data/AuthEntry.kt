package com.example.myapplication.data

/**
 * 模拟 Google Authenticator 的条目数据
 */
data class AuthEntry(
    val id: Int,
    val issuer: String,
    val accountName: String,
    val code: String,
    val remainingSeconds: Int,
    val iconInitial: String
)

/**
 * 提供模拟数据
 */
object SampleData {
    val entries = listOf(
        AuthEntry(1, "Google", "user@gmail.com", "482 901", 15, "G"),
        AuthEntry(2, "GitHub", "developer@github.com", "739 215", 22, "G"),
        AuthEntry(3, "Microsoft", "user@outlook.com", "158 463", 8, "M"),
        AuthEntry(4, "AWS", "admin@company.com", "927 384", 30, "A"),
        AuthEntry(5, "Discord", "gamer#1234", "361 847", 18, "D"),
        AuthEntry(6, "Twitter", "@user_handle", "542 196", 25, "T"),
        AuthEntry(7, "Facebook", "user@fb.com", "873 502", 12, "F"),
        AuthEntry(8, "Apple", "user@icloud.com", "294 618", 5, "A"),
        AuthEntry(9, "Dropbox", "user@dropbox.com", "615 374", 20, "D"),
        AuthEntry(10, "Slack", "workspace@slack.com", "486 923", 28, "S"),
        AuthEntry(11, "Netflix", "user@netflix.com", "137 594", 14, "N"),
        AuthEntry(12, "Amazon", "shopper@amazon.com", "852 461", 9, "A")
    )
}