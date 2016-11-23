CREATE TABLE `galleries` (
  `galleryID` int(10) unsigned NOT NULL auto_increment,
  `name` varchar(255) NOT NULL,
  `description` text NOT NULL,
  `alias` varchar(255) NOT NULL,
  `url` varchar(255) NOT NULL,
  `anonymousAccess` tinyint(1) unsigned NOT NULL,
  `userAccess` tinyint(1) unsigned NOT NULL,
  `adminAccess` tinyint(1) unsigned NOT NULL,
  PRIMARY KEY  (`galleryID`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;