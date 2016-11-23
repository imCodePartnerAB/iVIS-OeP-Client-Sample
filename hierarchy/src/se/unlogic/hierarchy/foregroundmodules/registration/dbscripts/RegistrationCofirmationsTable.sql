CREATE TABLE `registrationcofirmations` (
  `userID` int(10) unsigned NOT NULL,
  `linkID` varchar(36) NOT NULL,
  `host` text NOT NULL,
  `added` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  PRIMARY KEY  (`userID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;