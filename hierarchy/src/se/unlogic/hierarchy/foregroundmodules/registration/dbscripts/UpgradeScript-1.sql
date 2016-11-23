ALTER TABLE `registrationcofirmations` CHANGE COLUMN `ip` `host` TEXT CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
 ADD COLUMN `added` TIMESTAMP NOT NULL AFTER `host`;