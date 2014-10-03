CREATE TABLE tblCategory (
  ID_category INTEGER PRIMARY KEY,
  Name_category TEXT
);

CREATE TABLE tblProductList (
  ID_product INTEGER PRIMARY KEY,
  Name_product TEXT,
  ID_category INTEGER
);

CREATE TABLE tblPrivateNotes (
  ID_record INTEGER PRIMARY KEY,
  ID_product INTEGER,
  ID_category INTEGER,
  Comment TEXT
);