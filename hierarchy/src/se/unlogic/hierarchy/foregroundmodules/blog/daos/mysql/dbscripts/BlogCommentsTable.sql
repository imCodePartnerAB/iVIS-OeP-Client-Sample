CREATE TABLE  `blogcomments` (
  `commentID` int(10) unsigned NOT NULL auto_increment,
  `added` datetime NOT NULL,
  `updated` datetime default NULL,
  `message` text NOT NULL,
  `posterID` int(10) unsigned default NULL,
  `editorID` int(10) unsigned default NULL,
  `posterName` varchar(255) default NULL,
  `posterEmail` varchar(255) default NULL,
  `posterWebsite` varchar(255) default NULL,
  `postID` int(10) unsigned NOT NULL,
  PRIMARY KEY  (`commentID`),
  KEY `FK_comments_1` (`postID`),
  CONSTRAINT `FK_comments_1` FOREIGN KEY (`postID`) REFERENCES `blogposts` (`postID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;