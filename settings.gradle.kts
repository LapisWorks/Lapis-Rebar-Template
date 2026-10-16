pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}

// 复制模板后把这里改成你的项目名（会决定生成的 jar 文件名）
rootProject.name = "MyRebarAddon"
