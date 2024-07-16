-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jul 16, 2024 at 08:34 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `finalprojectbitp3123`
--

-- --------------------------------------------------------

--
-- Table structure for table `orders`
--

CREATE TABLE `orders` (
  `orderId` varchar(100) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `phone` varchar(20) NOT NULL,
  `deliveryTime` varchar(8) NOT NULL,
  `address` varchar(255) NOT NULL,
  `status` varchar(100) NOT NULL,
  `orderlist` varchar(1000) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `orders`
--

INSERT INTO `orders` (`orderId`, `name`, `phone`, `deliveryTime`, `address`, `status`, `orderlist`) VALUES
('#C0001', 'Aida', '0108607055', '5:35PM', 'No 38, Jalan Siantan 5, Taman Paya Rumput Perdana, 76450 Melaka', 'Accept', 'Hot Matcha Latte-1\nIced Caramel Macchiato-1\nCold Brew-1'),
('#C0002', 'Anis', '0198009090', '10:45AM', 'No 9, Jalan Axiata, Taman Meru, 41250 Klang', 'Decline', 'Honeydew Latte-1'),
('#C0003', 'Karina', '0198070880', '4PM', 'No 3, Jalan Aespa, Taman Kwangya, 76450 Melaka', 'Decline', 'Ice Americano-1'),
('#C0004', 'Hakim', '0109808099', '12PM', 'No 90, Jalan Jaya 1, Taman Kelana Jaya, 60950 Kuala Lumpur', 'Done', 'Iced Mocha-1\nIced Lemonade-1\nIced Latte-2'),
('#C0005', 'Hariz', '0109808077', '6:30PM', 'No 1, Jalan Matahari 3, Taman Bertam Perdana, 76450 Melaka', 'Done', 'Hot Latte-1\nHot Cappucino-1'),
('#C0008', 'Hana', '01345509870', '9:30AM', 'No 13, Jalan Perdana 5, Taman Bertam Perdana, 76450 Melaka', 'Done', 'Iced Buttercream Latte-1\nHot Cappucino-1'),
('#C0009', 'Ahmad', '01345509870', '9:30AM', 'No 13, Jalan Perdana 5, Taman Bertam Perdana, 76450 Melaka', 'Accept', 'Iced Buttercream Latte-1\n'),
('#C0010', 'Kamil', '01348809870', '11:30AM', 'No 10, Jalan Perdana 10, Taman Bertam , 76450 Melaka', 'Accept', 'Iced Buttercream Latte-1\n');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `orders`
--
ALTER TABLE `orders`
  ADD PRIMARY KEY (`orderId`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
