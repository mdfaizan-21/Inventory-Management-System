

--Show all products (only product names and categories):
SELECT ProductName, ProductCategory
FROM products;


--Find products where quantity is more than 10:

SELECT *
FROM products
WHERE AvailableQuantity > 10;


--Find products where price is less than 5000:

SELECT *
FROM products
WHERE Price < 5000;


--Show all Electronics products:

SELECT *
FROM products
WHERE ProductCategory = 'Electronics';


--Show all products sorted by price (highest first):

SELECT *
FROM products
ORDER BY Price DESC;


--Show the top 3 most expensive products:

SELECT *
FROM products
ORDER BY Price DESC
LIMIT 3;


--Find the total number of products (sum of quantity):

SELECT SUM(AvailableQuantity) AS total_quantity
FROM products;


--Find the average price of products:

SELECT AVG(Price) AS average_price
FROM products;


--Find the highest priced product:

SELECT *
FROM products
ORDER BY Price DESC
LIMIT 1;