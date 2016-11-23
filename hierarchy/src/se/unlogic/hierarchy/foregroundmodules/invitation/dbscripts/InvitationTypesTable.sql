CREATE TABLE  `invitationtypes` (
  `invitationTypeID` int(10) unsigned NOT NULL auto_increment,
  `name` varchar(255) NOT NULL,
  `subject` varchar(255) NOT NULL,
  `message` mediumtext NOT NULL,
  `senderName` varchar(255) NOT NULL,
  `senderEmail` varchar(255) NOT NULL,
  PRIMARY KEY  (`invitationTypeID`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;