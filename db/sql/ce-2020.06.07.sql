BEGIN TRANSACTION;
DROP TABLE IF EXISTS "categories";
CREATE TABLE IF NOT EXISTS "categories" (
	"id"	INTEGER NOT NULL UNIQUE,
	"first_parent_id"	INTEGER NOT NULL,
	"name"	TEXT NOT NULL,
	PRIMARY KEY("id" AUTOINCREMENT)
);
DROP TABLE IF EXISTS "items";
CREATE TABLE IF NOT EXISTS "items" (
	"id"	INTEGER NOT NULL UNIQUE,
	"text"	TEXT NOT NULL,
	"note"	TEXT NOT NULL,
	"entry_date_java"	INTEGER NOT NULL,
	"entry_date_cs"	INTEGER NOT NULL,
	"last_changed_java"	INTEGER NOT NULL,
	"last_changed_cs"	INTEGER NOT NULL,
	PRIMARY KEY("id" AUTOINCREMENT)
);
DROP TABLE IF EXISTS "category_views";
CREATE TABLE IF NOT EXISTS "category_views" (
	"id"	INTEGER NOT NULL UNIQUE,
	"view_id"	INTEGER NOT NULL,
	"category_id"	INTEGER NOT NULL,
	"position"	INTEGER NOT NULL,
	PRIMARY KEY("id" AUTOINCREMENT),
	CONSTRAINT "unique_category_per_view" UNIQUE("view_id","category_id"),
	FOREIGN KEY("view_id") REFERENCES "views"("id"),
	FOREIGN KEY("category_id") REFERENCES "categories"("id")
);
DROP TABLE IF EXISTS "views";
CREATE TABLE IF NOT EXISTS "views" (
	"id"	INTEGER NOT NULL UNIQUE,
	"parent_id"	INTEGER NOT NULL,
	"name"	TEXT NOT NULL UNIQUE,
	PRIMARY KEY("id" AUTOINCREMENT),
	CONSTRAINT "unique_name" UNIQUE("name")
);
DROP TABLE IF EXISTS "category_items";
CREATE TABLE IF NOT EXISTS "category_items" (
	"id"	INTEGER NOT NULL UNIQUE,
	"item_id"	INTEGER NOT NULL,
	"category_id"	INTEGER NOT NULL,
	PRIMARY KEY("id" AUTOINCREMENT),
	FOREIGN KEY("item_id") REFERENCES "items"("id"),
	FOREIGN KEY("category_id") REFERENCES "categories"("id")
);
DROP TABLE IF EXISTS "category_parents";
CREATE TABLE IF NOT EXISTS "category_parents" (
	"id"	INTEGER NOT NULL UNIQUE,
	"category_id"	INTEGER NOT NULL,
	"parent_id"	INTEGER NOT NULL,
	PRIMARY KEY("id" AUTOINCREMENT),
	FOREIGN KEY("category_id") REFERENCES "categories"("id"),
	FOREIGN KEY("parent_id") REFERENCES "categories"("id")
);
DROP INDEX IF EXISTS "categories_index";
CREATE INDEX IF NOT EXISTS "categories_index" ON "categories" (
	"name",
	"first_parent_id"
);
DROP INDEX IF EXISTS "items_index";
CREATE INDEX IF NOT EXISTS "items_index" ON "items" (
	"entry_date_java",
	"entry_date_cs"
);
DROP INDEX IF EXISTS "category_views_index";
CREATE INDEX IF NOT EXISTS "category_views_index" ON "category_views" (
	"view_id",
	"category_id"
);
DROP INDEX IF EXISTS "views_index";
CREATE INDEX IF NOT EXISTS "views_index" ON "views" (
	"parent_id",
	"name"
);
DROP INDEX IF EXISTS "category_items_index";
CREATE INDEX IF NOT EXISTS "category_items_index" ON "category_items" (
	"category_id",
	"item_id"
);
DROP INDEX IF EXISTS "category_parents_index";
CREATE INDEX IF NOT EXISTS "category_parents_index" ON "category_parents" (
	"category_id",
	"parent_id"
);
COMMIT;
