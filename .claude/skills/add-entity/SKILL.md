---
name: add-entity
description: Conventions for adding a new domain type or DB table to this app across the entity, repository, service and ui layers. Use when asked to add a feature, model, table or CRUD screen.
---

Model new code on the `Topic` slice: `entity/Topic.kt`, `repository/TopicRepository.kt`, `service/TopicService.kt`, `ui/App.kt`. Put one class per file in the matching `guru.bisser.<layer>` package.

1. **Entity** (`entity/`): a plain `data class`. Ids are `UUID`, and nullable foreign keys are `UUID?`.
2. **Repository** (`repository/`): raw JDBC only, with no ORM. Follow these rules:
   - The constructor takes `DatabaseConfig`. Open a connection per call with `connect().use { }`, and wrap every `Statement`, `PreparedStatement` and `ResultSet` in `.use { }`.
   - Store UUIDs as `BINARY(16)`. Reuse the `setUuid`/`toBytes`/`toUuid` helper pattern; if a second repository needs them, move them to a shared file instead of copying them.
   - Use a `PreparedStatement` with `?` parameters for every value. Use `<=>` when comparing a column that may be NULL.
   - Add a `createTableIfNotExists()` and call it from `Main.kt` next to the existing one. There are no migrations.
   - Map rows with a private `ResultSet.toX()` extension.
3. **Service** (`service/`): this layer holds validation (`require(...)`), id generation (`UUID.randomUUID()`) and ordering logic. The UI must not touch repositories.
4. **Wiring**: build the repository and service in `main()` (`Main.kt`) and pass the service into the composable.
5. **UI** (`ui/`): run service calls in `withContext(Dispatchers.IO)`, inside `LaunchedEffect` or `rememberCoroutineScope().launch`. Catch `SQLException` and show it through an `error` state. Name `@Composable` functions in PascalCase; ktlint allows this.
6. Run `/verify` at the end. If you can't test against a live MySQL database, say so: DB code needs `DB_URL`, `DB_USER` and `DB_PASSWORD`.
