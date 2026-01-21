-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1:3306
-- Généré le : lun. 19 jan. 2026 à 03:25
-- Version du serveur : 8.0.31
-- Version de PHP : 8.2.0

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `gestion_stock`
--

-- --------------------------------------------------------

--
-- Structure de la table `product`
--

DROP TABLE IF EXISTS `product`;
CREATE TABLE IF NOT EXISTS `product` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `quantity` int NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `category` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `product`
--

INSERT INTO `product` (`id`, `name`, `description`, `quantity`, `price`, `category`) VALUES
(2, 'Clavier', 'Clavier Gameur', 1, '30000.00', 'Informatique'),
(3, 'Souris', 'Souris Gameur', 15, '20000.00', 'Informatique'),
(4, 'Costard', 'Daro marque', 30, '180000.00', 'Vêtements'),
(5, 'Moccassin', 'Bote mocassin', 50, '80000.00', 'Vêtements'),
(6, 'Ram 8gb', 'ram ddr4 8gb', 50, '50000.00', 'Informatique'),
(7, 'Ecran 22 pouces', 'ecran marque TCL', 15, '300000.00', 'Informatique'),
(8, 'Airpods pro', 'Airpods pro black 2nd generation', 35, '40000.00', 'Informatique'),
(9, 'Tee-Shirt', 'Couleur Noir', 40, '25000.00', 'Vêtements'),
(10, 'Frommage', 'La vache qui rit', 50, '4000.00', 'Alimentation'),
(11, 'Yourt Nature', 'Sans sucre', 30, '2500.00', 'Alimentation'),
(12, 'Jord4', 'Couleur Black cat', 20, '50000.00', 'Vêtements'),
(13, 'UC semi-gamer', 'ram: 16GB , corei5 4th, 1to SSD', 10, '800000.00', 'Informatique');

-- --------------------------------------------------------

--
-- Structure de la table `stock_movement`
--

DROP TABLE IF EXISTS `stock_movement`;
CREATE TABLE IF NOT EXISTS `stock_movement` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `movement_date` datetime(6) NOT NULL,
  `movement_type` varchar(10) NOT NULL,
  `note` varchar(255) DEFAULT NULL,
  `quantity` int NOT NULL,
  `product_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKq63e7y5l2pnh2tt2lvxlquvbf` (`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `stock_movement`
--

INSERT INTO `stock_movement` (`id`, `movement_date`, `movement_type`, `note`, `quantity`, `product_id`) VALUES
(1, '2026-01-15 02:26:33.582000', 'ENTREE', 'SDCDSCDSCDSVCSD', 20, 2),
(2, '2026-01-15 02:27:36.021000', 'SORTIE', 'QSDSFDS', 10, 4),
(3, '2026-01-15 02:28:41.242000', 'SORTIE', 'QSDQDQSDQSD', 39, 2),
(4, '2026-01-15 17:21:29.484000', 'ENTREE', 'PRODUIT AIRPODS ENTRER', 30, 8),
(5, '2026-01-15 17:21:47.666000', 'SORTIE', 'Vente', 3, 8),
(6, '2026-01-15 17:22:00.658000', 'SORTIE', 'Vente', 5, 8),
(7, '2026-01-15 17:22:15.532000', 'SORTIE', 'Vente', 10, 8),
(8, '2026-01-15 17:23:08.102000', 'SORTIE', 'Vente', 7, 8);

-- --------------------------------------------------------

--
-- Structure de la table `user_account`
--

DROP TABLE IF EXISTS `user_account`;
CREATE TABLE IF NOT EXISTS `user_account` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `full_name` varchar(100) DEFAULT NULL,
  `password_hash` varchar(128) NOT NULL,
  `username` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_castjbvpeeus0r8lbpehiu0e4` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `user_account`
--

INSERT INTO `user_account` (`id`, `full_name`, `password_hash`, `username`) VALUES
(1, 'Miguel Singcol', '389e5cbfd4fca6fe3ed9e1cd4a83f6d5da8e9c9070e38358bfc82d724a1fd0f7', 'Bayane'),
(2, 'Goulzaraly', '389e5cbfd4fca6fe3ed9e1cd4a83f6d5da8e9c9070e38358bfc82d724a1fd0f7', 'Kevine');

--
-- Contraintes pour les tables déchargées
--

--
-- Contraintes pour la table `stock_movement`
--
ALTER TABLE `stock_movement`
  ADD CONSTRAINT `FKq63e7y5l2pnh2tt2lvxlquvbf` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
