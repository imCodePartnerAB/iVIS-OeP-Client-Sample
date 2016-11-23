CREATE TABLE `galleryusers` (
  `galleryID` int(10) unsigned NOT NULL auto_increment,
  `userID` int(10) unsigned NOT NULL,
  PRIMARY KEY  (`galleryID`,`userID`),
  CONSTRAINT `FK_galleryusers_1` FOREIGN KEY (`galleryID`) REFERENCES `galleries` (`galleryID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;