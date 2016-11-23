CREATE TABLE  `invitations` (
  `invitationID` int(10) unsigned NOT NULL auto_increment,
  `invitationTypeID` int(10) unsigned NOT NULL,
  `email` varchar(255) NOT NULL,
  `firstname` varchar(255) NOT NULL,
  `lastname` varchar(255) NOT NULL,
  `linkID` varchar(36) NOT NULL,
  `sendCount` int(10) unsigned NOT NULL,
  `lastSent` datetime default NULL,
  PRIMARY KEY  (`invitationID`),
  UNIQUE KEY `Index_3` (`email`),
  KEY `FK_Invitations_1` (`invitationTypeID`),
  CONSTRAINT `FK_Invitations_1` FOREIGN KEY (`invitationTypeID`) REFERENCES `invitationtypes` (`invitationTypeID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;