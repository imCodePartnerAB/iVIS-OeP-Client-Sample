CREATE TABLE `attachments` (
  `attachmenID` char(36) NOT NULL,
  `emailID` char(36) NOT NULL,
  `data` longblob NOT NULL,
  PRIMARY KEY  (`attachmenID`),
  KEY `FK_attachments_1` (`emailID`),
  CONSTRAINT `FK_attachments_1` FOREIGN KEY (`emailID`) REFERENCES `emails` (`emailID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;