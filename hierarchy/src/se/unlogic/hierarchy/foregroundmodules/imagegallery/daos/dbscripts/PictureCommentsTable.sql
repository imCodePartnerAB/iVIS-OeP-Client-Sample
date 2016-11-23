CREATE TABLE `picturecomments` (
  `commentID` int(10) unsigned NOT NULL auto_increment,
  `pictureID` int(10) unsigned NOT NULL,
  `comment` text NOT NULL,
  `date` timestamp NOT NULL default '0000-00-00 00:00:00',
  `userID` int(10) unsigned default NULL,
  PRIMARY KEY  (`commentID`),
  KEY `FK_pictureComments_1` (`pictureID`),
  CONSTRAINT `FK_pictureComments_1` FOREIGN KEY (`pictureID`) REFERENCES `pictures` (`pictureID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;