CREATE TABLE `emails` (
  `emailID` char(36) NOT NULL,
  `resendCount` int(10) unsigned default NULL,
  `senderName` varchar(255) default NULL,
  `senderAddress` varchar(255) NOT NULL,
  `charset` varchar(255) default NULL,
  `messageContentType` varchar(255) default NULL,
  `subject` mediumtext,
  `message` longtext,
  `lastSent` timestamp NULL default NULL,
  `owner` int(10) unsigned default NULL,
  PRIMARY KEY  (`emailID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;