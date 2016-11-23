CREATE TABLE `gallerygroups` (
  `galleryID` int(10) unsigned NOT NULL auto_increment,
  `groupID` int(10) unsigned NOT NULL,
  PRIMARY KEY  (`galleryID`,`groupID`),
  CONSTRAINT `FK_gallerygroups_1` FOREIGN KEY (`galleryID`) REFERENCES `galleries` (`galleryID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;