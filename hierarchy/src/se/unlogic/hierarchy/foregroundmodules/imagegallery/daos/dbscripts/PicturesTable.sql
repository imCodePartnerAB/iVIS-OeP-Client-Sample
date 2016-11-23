CREATE TABLE `pictures` (
  `pictureID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `filename` varchar(255) NOT NULL,
  `smallThumb` blob NOT NULL,
  `mediumThumb` mediumblob NOT NULL,
  `galleryID` int(10) unsigned NOT NULL,
  PRIMARY KEY (`pictureID`),
  UNIQUE KEY `gallery_filename_index` (`galleryID`,`filename`),
  KEY `FK_image_galleryID` (`galleryID`),
  CONSTRAINT `FK_image_galleryID` FOREIGN KEY (`galleryID`) REFERENCES `galleries` (`galleryID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;