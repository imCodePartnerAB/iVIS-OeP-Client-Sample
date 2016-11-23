CREATE TABLE  `invitationtypegroups` (
  `invitationTypeID` int(10) unsigned NOT NULL auto_increment,
  `groupID` int(10) unsigned NOT NULL,
  PRIMARY KEY  USING BTREE (`invitationTypeID`,`groupID`),
  CONSTRAINT `FK_invitationtypegroups_1` FOREIGN KEY (`invitationTypeID`) REFERENCES `invitationtypes` (`invitationTypeID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;