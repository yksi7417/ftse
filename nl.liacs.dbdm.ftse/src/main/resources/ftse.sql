SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

DROP SCHEMA IF EXISTS `ftse` ;
CREATE SCHEMA IF NOT EXISTS `ftse` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci ;
USE `ftse`;

-- -----------------------------------------------------
-- Table `ftse`.`ftse`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `ftse`.`ftse` ;

CREATE  TABLE IF NOT EXISTS `ftse`.`ftse` (
  `ftse_id` INT NOT NULL AUTO_INCREMENT ,
  `ftse_date` DATETIME NOT NULL ,
  `ftse_open` FLOAT NOT NULL ,
  `ftse_low` FLOAT NOT NULL ,
  `ftse_high` FLOAT NOT NULL ,
  `ftse_close` FLOAT NOT NULL ,
  `ftse_volume` FLOAT NULL DEFAULT 0 ,
  `ftse_adj_close` FLOAT NULL DEFAULT 0 ,
  PRIMARY KEY (`ftse_id`) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;



SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

