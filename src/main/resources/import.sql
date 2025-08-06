-- --Departamentos:
INSERT INTO `stella`.`departamentos` (`nombre`) VALUES ('Alimentos');
INSERT INTO `stella`.`departamentos` (`nombre`) VALUES ('Bebidas');
INSERT INTO `stella`.`departamentos` (`nombre`) VALUES ('Limpieza');
INSERT INTO `stella`.`departamentos` (`nombre`) VALUES ('Higiene Personal');
INSERT INTO `stella`.`departamentos` (`nombre`) VALUES ('Frescos');
INSERT INTO `stella`.`departamentos` (`nombre`) VALUES ('Congelados');
INSERT INTO `stella`.`departamentos` (`nombre`) VALUES ('Panadería');
INSERT INTO `stella`.`departamentos` (`nombre`) VALUES ('Lácteos');
INSERT INTO `stella`.`departamentos` (`nombre`) VALUES ('Mascotas');
INSERT INTO `stella`.`departamentos` (`nombre`) VALUES ('Bazar');


-- Alimentos (1)
INSERT INTO `stella`.`categorias` (`nombre`, `departamento_id`) VALUES ('Sopas y Caldos', 1);
INSERT INTO `stella`.`categorias` (`nombre`, `departamento_id`) VALUES ('Snacks y Galletitas', 1);
INSERT INTO `stella`.`categorias` (`nombre`, `departamento_id`) VALUES ('Cereales', 1);
INSERT INTO `stella`.`categorias` (`nombre`, `departamento_id`) VALUES ('Especias y Condimentos', 1);
INSERT INTO `stella`.`categorias` (`nombre`, `departamento_id`) VALUES ('Salsas y Aderezos', 1);

-- Bebidas (2)
INSERT INTO `stella`.`categorias` (`nombre`, `departamento_id`) VALUES ('Energizantes', 2);
INSERT INTO `stella`.`categorias` (`nombre`, `departamento_id`) VALUES ('Bebidas Deportivas', 2);
INSERT INTO `stella`.`categorias` (`nombre`, `departamento_id`) VALUES ('Tés e Infusiones', 2);
INSERT INTO `stella`.`categorias` (`nombre`, `departamento_id`) VALUES ('Café', 2);
INSERT INTO `stella`.`categorias` (`nombre`, `departamento_id`) VALUES ('Vinos y Espumantes', 2);

-- Limpieza (3)
INSERT INTO `stella`.`categorias` (`nombre`, `departamento_id`) VALUES ('Suavizantes', 3);
INSERT INTO `stella`.`categorias` (`nombre`, `departamento_id`) VALUES ('Jabón en Polvo', 3);
INSERT INTO `stella`.`categorias` (`nombre`, `departamento_id`) VALUES ('Limpiahornos', 3);
INSERT INTO `stella`.`categorias` (`nombre`, `departamento_id`) VALUES ('Insecticidas', 3);
INSERT INTO `stella`.`categorias` (`nombre`, `departamento_id`) VALUES ('Desodorantes de Ambiente', 3);

-- Higiene Personal (4)
INSERT INTO `stella`.`categorias` (`nombre`, `departamento_id`) VALUES ('Papel Higiénico', 4);
INSERT INTO `stella`.`categorias` (`nombre`, `departamento_id`) VALUES ('Toallas Femeninas', 4);
INSERT INTO `stella`.`categorias` (`nombre`, `departamento_id`) VALUES ('Cepillos y Pasta Dental', 4);
INSERT INTO `stella`.`categorias` (`nombre`, `departamento_id`) VALUES ('Afeitado y Depilación', 4);
INSERT INTO `stella`.`categorias` (`nombre`, `departamento_id`) VALUES ('Cremas y Lociones', 4);

-- Frescos (5)
INSERT INTO `stella`.`categorias` (`nombre`, `departamento_id`) VALUES ('Pollo', 5);
INSERT INTO `stella`.`categorias` (`nombre`, `departamento_id`) VALUES ('Pescados y Mariscos', 5);
INSERT INTO `stella`.`categorias` (`nombre`, `departamento_id`) VALUES ('Embutidos', 5);
INSERT INTO `stella`.`categorias` (`nombre`, `departamento_id`) VALUES ('Huevos', 5);
INSERT INTO `stella`.`categorias` (`nombre`, `departamento_id`) VALUES ('Pastas Frescas', 5);

-- Congelados (6)
INSERT INTO `stella`.`categorias` (`nombre`, `departamento_id`) VALUES ('Pizzas Congeladas', 6);
INSERT INTO `stella`.`categorias` (`nombre`, `departamento_id`) VALUES ('Postres Congelados', 6);
INSERT INTO `stella`.`categorias` (`nombre`, `departamento_id`) VALUES ('Pollo Congelado', 6);
INSERT INTO `stella`.`categorias` (`nombre`, `departamento_id`) VALUES ('Papas Congeladas', 6);
INSERT INTO `stella`.`categorias` (`nombre`, `departamento_id`) VALUES ('Frutas Congeladas', 6);

-- Panadería (7)
INSERT INTO `stella`.`categorias` (`nombre`, `departamento_id`) VALUES ('Pan Dulce y Budines', 7);
INSERT INTO `stella`.`categorias` (`nombre`, `departamento_id`) VALUES ('Masas Finas', 7);
INSERT INTO `stella`.`categorias` (`nombre`, `departamento_id`) VALUES ('Pan Integral', 7);
INSERT INTO `stella`.`categorias` (`nombre`, `departamento_id`) VALUES ('Bizcochuelos', 7);
INSERT INTO `stella`.`categorias` (`nombre`, `departamento_id`) VALUES ('Medialunas', 7);

-- Lácteos (8)
INSERT INTO `stella`.`categorias` (`nombre`, `departamento_id`) VALUES ('Postres Lácteos', 8);
INSERT INTO `stella`.`categorias` (`nombre`, `departamento_id`) VALUES ('Manteca y Margarina', 8);
INSERT INTO `stella`.`categorias` (`nombre`, `departamento_id`) VALUES ('Leche Vegetal', 8);
INSERT INTO `stella`.`categorias` (`nombre`, `departamento_id`) VALUES ('Leche Condensada', 8);
INSERT INTO `stella`.`categorias` (`nombre`, `departamento_id`) VALUES ('Queso Untable', 8);

-- Mascotas (9)
INSERT INTO `stella`.`categorias` (`nombre`, `departamento_id`) VALUES ('Snacks para Mascotas', 9);
INSERT INTO `stella`.`categorias` (`nombre`, `departamento_id`) VALUES ('Higiene de Mascotas', 9);
INSERT INTO `stella`.`categorias` (`nombre`, `departamento_id`) VALUES ('Juguetes para Mascotas', 9);
INSERT INTO `stella`.`categorias` (`nombre`, `departamento_id`) VALUES ('Arena para Gatos', 9);
INSERT INTO `stella`.`categorias` (`nombre`, `departamento_id`) VALUES ('Accesorios de Paseo', 9);

-- Bazar (10)
INSERT INTO `stella`.`categorias` (`nombre`, `departamento_id`) VALUES ('Contenedores y Tuppers', 10);
INSERT INTO `stella`.`categorias` (`nombre`, `departamento_id`) VALUES ('Utensilios de Limpieza', 10);
INSERT INTO `stella`.`categorias` (`nombre`, `departamento_id`) VALUES ('Iluminación', 10);
INSERT INTO `stella`.`categorias` (`nombre`, `departamento_id`) VALUES ('Organización del Hogar', 10);
INSERT INTO `stella`.`categorias` (`nombre`, `departamento_id`) VALUES ('Termos y Botellas', 10);

INSERT INTO `stella`.`roles` (`nombre`) VALUES ("Administrador");
INSERT INTO `stella`.`roles` (`nombre`) VALUES ("Repositor");
INSERT INTO `stella`.`roles` (`nombre`) VALUES ("Cajero");
INSERT INTO `stella`.`roles` (`nombre`) VALUES ("SuperUser");