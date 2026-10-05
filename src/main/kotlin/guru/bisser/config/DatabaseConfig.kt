package guru.bisser.config

class DatabaseConfig {
    val url = System.getenv("DB_URL")
    val user = System.getenv("DB_USER")
    val password = System.getenv("DB_PASSWORD")
}
