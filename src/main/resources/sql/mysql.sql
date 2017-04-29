CREATE TABLE `{SQLPREFIX}guilds` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `uuid` tinytext CHARACTER SET utf8 NOT NULL,
  `tag` tinytext CHARACTER SET utf8 NOT NULL,
  `name` tinytext CHARACTER SET utf8 NOT NULL,
  `leader` tinytext CHARACTER SET utf8 NOT NULL,
  `spawn` tinytext CHARACTER SET utf8 NOT NULL,
  `allies` text CHARACTER SET utf8 NOT NULL,
  `alliesinv` text CHARACTER SET utf8 NOT NULL,
  `war` text CHARACTER SET utf8 NOT NULL,
  `nowarinv` text CHARACTER SET utf8 NOT NULL,
  `money` double NOT NULL,
  `points` int(11) unsigned NOT NULL,
  `lives` int(11) NOT NULL,
  `timerest` int(11) NOT NULL,
  `lostlive` int(11) NOT NULL,
  `activity` int(11) NOT NULL,
  `created` int(11) NOT NULL,
  `bankloc` tinytext NOT NULL,
  `slots` int(11) NOT NULL,
  `openinv` int(1) NOT NULL,
  `banner` text NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;
--
CREATE TABLE `{SQLPREFIX}players` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `uuid` tinytext CHARACTER SET utf8 NOT NULL,
  `name` tinytext CHARACTER SET utf8 NOT NULL,
  `guild` tinytext CHARACTER SET utf8 NOT NULL,
  `invitedto` text CHARACTER SET utf8 NOT NULL,
  `points` int(11) NOT NULL,
  `kills` int(11) NOT NULL,
  `deaths` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;
--
CREATE TABLE `{SQLPREFIX}regions` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `uuid` tinytext CHARACTER SET utf8 NOT NULL,
  `loc_1` tinytext CHARACTER SET utf8 NOT NULL,
  `loc_2` tinytext CHARACTER SET utf8 NOT NULL,
  `guild` tinytext CHARACTER SET utf8 NOT NULL,
  `world` tinytext CHARACTER SET utf8 NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;
--
CREATE TABLE `{SQLPREFIX}ranks` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `uuid` tinytext CHARACTER SET utf8 NOT NULL,
  `name` tinytext CHARACTER SET utf8 NOT NULL,
  `guild` tinytext CHARACTER SET utf8 NOT NULL,
  `permissions` text CHARACTER SET utf8 NOT NULL,
  `members` text CHARACTER SET utf8 NOT NULL,
  `def` int(1) unsigned NOT NULL,
  `clone` int(1) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;
