CREATE TABLE `statsgrid` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `date` datetime DEFAULT NULL,
  `time` datetime DEFAULT NULL,
  `symbol` varchar(255) NOT NULL,
  `volume` bigint(20) NOT NULL,
  `mean` double NULL,
  `stdev` double NULL,
  `min` bigint(20) NOT NULL,
  `max` bigint(20) NOT NULL,
  `normaldist` double NULL,
  `samplesize` bigint(20) NOT NULL
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7697273 DEFAULT CHARSET=latin1