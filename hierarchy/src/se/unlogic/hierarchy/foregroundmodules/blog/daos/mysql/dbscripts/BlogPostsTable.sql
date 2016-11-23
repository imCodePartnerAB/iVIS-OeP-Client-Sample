CREATE TABLE `blogposts` (
  `postID` int(10) unsigned NOT NULL auto_increment,
  `alias` varchar(255) NOT NULL,
  `added` datetime NOT NULL,
  `updated` datetime default NULL,
  `title` varchar(255) NOT NULL,
  `message` mediumtext NOT NULL,
  `posterID` int(10) unsigned NOT NULL,
  `editorID` int(10) unsigned default NULL,
  `readCount` int(10) unsigned NOT NULL default '0',
  `split` tinyint(1) NOT NULL,
  `blogID` varchar(255) NOT NULL,
  PRIMARY KEY  (`postID`),
  UNIQUE KEY `Index_2` USING BTREE (`alias`,`blogID`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;