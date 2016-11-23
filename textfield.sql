DROP TABLE `ivis_text_field_queries`;
DROP TABLE `ivis_text_fields`;
DROP TABLE `ivis_text_field_query_instances`;
DROP TABLE `ivis_text_field_query_instance_values`;

CREATE TABLE IF NOT EXISTS ivis_text_field_queries
(
  queryID INT UNSIGNED PRIMARY KEY NOT NULL,
  description LONGTEXT,
  helpText LONGTEXT,
  layout VARCHAR(45) NOT NULL
);
CREATE TABLE ivis_text_field_query_instance_values
(
  textFieldValueID INT UNSIGNED PRIMARY KEY NOT NULL AUTO_INCREMENT,
  queryInstanceID INT UNSIGNED NOT NULL,
  textFieldID INT UNSIGNED NOT NULL,
  value VARCHAR(255) NOT NULL
);
CREATE TABLE ivis_text_field_query_instances
(
  queryInstanceID INT UNSIGNED PRIMARY KEY NOT NULL,
  queryID INT UNSIGNED NOT NULL
);
CREATE TABLE ivis_text_fields
(
  textFieldID INT UNSIGNED PRIMARY KEY NOT NULL AUTO_INCREMENT,
  label VARCHAR(255),
  required TINYINT NOT NULL,
  width INT UNSIGNED,
  sortIndex INT UNSIGNED NOT NULL,
  formatValidator VARCHAR(255),
  queryID INT UNSIGNED NOT NULL,
  maxContentLength INT UNSIGNED,
  invalidFormatMessage VARCHAR(255),
  setAsAttribute TINYINT NOT NULL,
  attributeName VARCHAR(255),
  `xsdElementName` varchar(255) DEFAULT NULL,
  `exported` tinyint(1) NOT NULL
);
ALTER TABLE ivis_text_field_query_instance_values ADD FOREIGN KEY (queryInstanceID) REFERENCES ivis_text_field_query_instances (queryInstanceID) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE ivis_text_field_query_instance_values ADD FOREIGN KEY (textFieldID) REFERENCES ivis_text_fields (textFieldID) ON DELETE CASCADE ON UPDATE CASCADE;
CREATE INDEX FK_ivis_text_field_query_instance_values_1 ON ivis_text_field_query_instance_values (queryInstanceID);
CREATE INDEX FK_ivis_text_field_query_instance_values_2 ON ivis_text_field_query_instance_values (textFieldID);
ALTER TABLE ivis_text_field_query_instances ADD FOREIGN KEY (queryID) REFERENCES ivis_text_field_queries (queryID) ON DELETE CASCADE ON UPDATE CASCADE;
CREATE INDEX FK_ivis_text_field_query_instances_1 ON ivis_text_field_query_instances (queryID);
ALTER TABLE ivis_text_fields ADD FOREIGN KEY (queryID) REFERENCES ivis_text_field_queries (queryID) ON DELETE CASCADE ON UPDATE CASCADE;
CREATE INDEX FK_ivis_ivis_text_fields_1 ON ivis_text_fields (queryID);
CREATE INDEX FK_ivis_text_fields_1 ON ivis_text_fields (queryID);
