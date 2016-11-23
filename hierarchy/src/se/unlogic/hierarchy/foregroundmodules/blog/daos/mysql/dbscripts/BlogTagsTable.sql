CREATE TABLE `blogtags` (
  `postID` int(10) unsigned NOT NULL,
  `tag` varchar(255) NOT NULL,
  PRIMARY KEY  USING BTREE (`postID`,`tag`),
  CONSTRAINT `FK_tags_1` FOREIGN KEY (`postID`) REFERENCES `blogposts` (`postID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;