-- --Departamentos:
-- INSERT INTO `stella`.`departamentos` (`nombre`) VALUES ('Alimentos');
-- INSERT INTO `stella`.`departamentos` (`nombre`) VALUES ('Bebidas');
-- INSERT INTO `stella`.`departamentos` (`nombre`) VALUES ('Limpieza');
-- INSERT INTO `stella`.`departamentos` (`nombre`) VALUES ('Higiene Personal');
-- INSERT INTO `stella`.`departamentos` (`nombre`) VALUES ('Frescos');
-- INSERT INTO `stella`.`departamentos` (`nombre`) VALUES ('Congelados');
-- INSERT INTO `stella`.`departamentos` (`nombre`) VALUES ('Panadería');
-- INSERT INTO `stella`.`departamentos` (`nombre`) VALUES ('Lácteos');
-- INSERT INTO `stella`.`departamentos` (`nombre`) VALUES ('Mascotas');
-- INSERT INTO `stella`.`departamentos` (`nombre`) VALUES ('Bazar');

-- --Alimentos:
-- INSERT INTO `stella`.`categorias` (`nombre`) VALUES ('Sopas y Caldos');
-- INSERT INTO `stella`.`categorias` (`nombre`) VALUES ('Snacks y Galletitas');
-- INSERT INTO `stella`.`categorias` (`nombre`) VALUES ('Cereales');
-- INSERT INTO `stella`.`categorias` (`nombre`) VALUES ('Especias y Condimentos');
-- INSERT INTO `stella`.`categorias` (`nombre`) VALUES ('Salsas y Aderezos');


-- --Bebidas
-- INSERT INTO `stella`.`categorias` (`nombre`) VALUES ('Energizantes');
-- INSERT INTO `stella`.`categorias` (`nombre`) VALUES ('Bebidas Deportivas');
-- INSERT INTO `stella`.`categorias` (`nombre`) VALUES ('Tés e Infusiones');
-- INSERT INTO `stella`.`categorias` (`nombre`) VALUES ('Café');
-- INSERT INTO `stella`.`categorias` (`nombre`) VALUES ('Vinos y Espumantes');


-- --Limpieza
-- INSERT INTO `stella`.`categorias` (`nombre`) VALUES ('Suavizantes');
-- INSERT INTO `stella`.`categorias` (`nombre`) VALUES ('Jabón en Polvo');
-- INSERT INTO `stella`.`categorias` (`nombre`) VALUES ('Limpiahornos');
-- INSERT INTO `stella`.`categorias` (`nombre`) VALUES ('Insecticidas');
-- INSERT INTO `stella`.`categorias` (`nombre`) VALUES ('Desodorantes de Ambiente');


-- --Higiene Personal
-- INSERT INTO `stella`.`categorias` (`nombre`) VALUES ('Papel Higiénico');
-- INSERT INTO `stella`.`categorias` (`nombre`) VALUES ('Toallas Femeninas');
-- INSERT INTO `stella`.`categorias` (`nombre`) VALUES ('Cepillos y Pasta Dental');
-- INSERT INTO `stella`.`categorias` (`nombre`) VALUES ('Afeitado y Depilación');
-- INSERT INTO `stella`.`categorias` (`nombre`) VALUES ('Cremas y Lociones');


-- --Frescos
-- INSERT INTO `stella`.`categorias` (`nombre`) VALUES ('Pollo');
-- INSERT INTO `stella`.`categorias` (`nombre`) VALUES ('Pescados y Mariscos');
-- INSERT INTO `stella`.`categorias` (`nombre`) VALUES ('Embutidos');
-- INSERT INTO `stella`.`categorias` (`nombre`) VALUES ('Huevos');
-- INSERT INTO `stella`.`categorias` (`nombre`) VALUES ('Pastas Frescas');


-- --Congelados
-- INSERT INTO `stella`.`categorias` (`nombre`) VALUES ('Pizzas Congeladas');
-- INSERT INTO `stella`.`categorias` (`nombre`) VALUES ('Postres Congelados');
-- INSERT INTO `stella`.`categorias` (`nombre`) VALUES ('Pollo Congelado');
-- INSERT INTO `stella`.`categorias` (`nombre`) VALUES ('Papas Congeladas');
-- INSERT INTO `stella`.`categorias` (`nombre`) VALUES ('Frutas Congeladas');


-- --Panaderia
-- INSERT INTO `stella`.`categorias` (`nombre`) VALUES ('Pan Dulce y Budines');
-- INSERT INTO `stella`.`categorias` (`nombre`) VALUES ('Masas Finas');
-- INSERT INTO `stella`.`categorias` (`nombre`) VALUES ('Pan Integral');
-- INSERT INTO `stella`.`categorias` (`nombre`) VALUES ('Bizcochuelos');
-- INSERT INTO `stella`.`categorias` (`nombre`) VALUES ('Medialunas');

-- --Lacteos
-- INSERT INTO `stella`.`categorias` (`nombre`) VALUES ('Postres Lácteos');
-- INSERT INTO `stella`.`categorias` (`nombre`) VALUES ('Manteca y Margarina');
-- INSERT INTO `stella`.`categorias` (`nombre`) VALUES ('Leche Vegetal');
-- INSERT INTO `stella`.`categorias` (`nombre`) VALUES ('Leche Condensada');
-- INSERT INTO `stella`.`categorias` (`nombre`) VALUES ('Queso Untable');


-- --Mascotas
-- INSERT INTO `stella`.`categorias` (`nombre`) VALUES ('Snacks para Mascotas');
-- INSERT INTO `stella`.`categorias` (`nombre`) VALUES ('Higiene de Mascotas');
-- INSERT INTO `stella`.`categorias` (`nombre`) VALUES ('Juguetes para Mascotas');
-- INSERT INTO `stella`.`categorias` (`nombre`) VALUES ('Arena para Gatos');
-- INSERT INTO `stella`.`categorias` (`nombre`) VALUES ('Accesorios de Paseo');


-- --Bazar
-- INSERT INTO `stella`.`categorias` (`nombre`) VALUES ('Contenedores y Tuppers');
-- INSERT INTO `stella`.`categorias` (`nombre`) VALUES ('Utensilios de Limpieza');
-- INSERT INTO `stella`.`categorias` (`nombre`) VALUES ('Iluminación');
-- INSERT INTO `stella`.`categorias` (`nombre`) VALUES ('Organización del Hogar');
-- INSERT INTO `stella`.`categorias` (`nombre`) VALUES ('Termos y Botellas');

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
