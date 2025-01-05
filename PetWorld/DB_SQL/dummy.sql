INSERT INTO PetSpecies (species_name, avg_min_price, avg_max_price, avg_weight, avg_max_age, image_url, des)
VALUES 
('Tiny Poodle', 100000, 10000000, 4, 14, 'https://github.com/vantuong147/ImageAssets/blob/main/PetShop/tiny_poodle.jpg?raw=true', 'Chó poodle tiny'),
('Golden', 5000000, 12000000, 20, 16, 'https://github.com/vantuong147/ImageAssets/blob/main/PetShop/dog.jpeg?raw=true', 'Chó golden hay'),
('Cat', 3000000, 15000000, 8, 18, 'https://github.com/vantuong147/ImageAssets/blob/main/PetShop/cat.jpg?raw=true', 'Cats are independent, elegant, and very clean animals.'),
('Rabbit', 500000, 2000000, 2, 12, 'https://github.com/vantuong147/ImageAssets/blob/main/PetShop/rabbit.jpg?raw=true', 'Rabbits are small, friendly, and can be kept indoors or outdoors.'),
('Hamster', 100000, 400000, 0.3, 3, 'https://github.com/vantuong147/ImageAssets/blob/main/PetShop/hamster.jpg?raw=true', 'Hamsters are small, nocturnal rodents and are popular as pets for children.'),
('Parrot', 1500000, 2000000, 1, 60, 'https://github.com/vantuong147/ImageAssets/blob/main/PetShop/parrot.jpg?raw=true', 'Parrots are colorful, intelligent birds that are known for their ability to mimic sounds.'),
('Goldfish', 10000, 500000, 0.1, 5, 'https://github.com/vantuong147/ImageAssets/blob/main/PetShop/goldfish.jpg?raw=true', 'Goldfish are small, ornamental fish commonly kept in aquariums.'),
('Guinea Pig', 2000000, 10000000, 1.5, 8, 'https://github.com/vantuong147/ImageAssets/blob/main/PetShop/Guinea_Pig.jpg?raw=true', 'Guinea pigs are social, gentle, and enjoy living in pairs or groups.'),
('Turtle', 5000000, 50000000, 2, 100, 'https://github.com/vantuong147/ImageAssets/blob/main/PetShop/turtle.jpg?raw=true', 'Turtles are slow-moving reptiles that can live both on land and in water.'),
('Snake', 3000000, 15000000, 1, 20, 'https://github.com/vantuong147/ImageAssets/blob/main/PetShop/snake.jpg?raw=true', 'Snakes are legless reptiles that can be found in a variety of environments.'),
('Frog', 1000000, 10000000, 0.2, 10, 'https://github.com/vantuong147/ImageAssets/blob/main/PetShop/frog.jpg?raw=true', 'Frogs are amphibians known for their jumping abilities and croaking sounds.'),
('Chinchilla', 10000000, 30000000, 1, 15, 'https://github.com/vantuong147/ImageAssets/blob/main/PetShop/chinchilla.jpg?raw=true', 'Chinchillas are small, nocturnal rodents known for their extremely soft fur.'),
('Ferret', 10000000, 30000000, 1, 10, 'https://github.com/vantuong147/ImageAssets/blob/main/PetShop/ferret.jpg?raw=true', 'Ferrets are playful, curious, and social animals that love to explore.');






-- Dummy data for Pet table with state 'NOT_SALED'

INSERT INTO Pet (pet_name, price, age, color, description, state, pet_species_id, images, weight)
VALUES 
('Tiny Poodle 1', 500000, 2, '#D2B48C', 'A cute and playful tiny poodle.', 'NOT_SALED', 16, 'https://github.com/vantuong147/ImageAssets/blob/main/PetShop/tiny_poodle.jpg?raw=true', 4),
('Tiny Poodle 2', 800000, 3, '#D2B48C', 'A tiny poodle with a fluffy coat.', 'NOT_SALED', 16, 'https://github.com/vantuong147/ImageAssets/blob/main/PetShop/tiny_poodle.jpg?raw=true', 4),
('Golden Retriever 1', 7000000, 4, '#FFD700', 'A friendly golden retriever that loves water.', 'NOT_SALED', 17, 'https://github.com/vantuong147/ImageAssets/blob/main/PetShop/dog.jpeg?raw=true', 20),
('Golden Retriever 2', 9000000, 5, '#FFD700', 'A trained golden retriever, excellent for families.', 'NOT_SALED', 17, 'https://github.com/vantuong147/ImageAssets/blob/main/PetShop/dog.jpeg?raw=true', 20),
('Persian Cat 1', 12000000, 2, '#B9B9B9', 'A fluffy Persian cat with a calm demeanor.', 'NOT_SALED', 18, 'https://github.com/vantuong147/ImageAssets/blob/main/PetShop/cat.jpg?raw=true', 8),
('Persian Cat 2', 15000000, 3, '#B9B9B9', 'A charming Persian cat, loves to be pampered.', 'NOT_SALED', 18, 'https://github.com/vantuong147/ImageAssets/blob/main/PetShop/cat.jpg?raw=true', 8),
('Bunny Rabbit 1', 1500000, 1, '#F4A300', 'A cute bunny rabbit that loves to hop around.', 'NOT_SALED', 19, 'https://github.com/vantuong147/ImageAssets/blob/main/PetShop/rabbit.jpg?raw=true', 2),
('Bunny Rabbit 2', 2000000, 2, '#F4A300', 'A playful bunny that enjoys companionship.', 'NOT_SALED', 19, 'https://github.com/vantuong147/ImageAssets/blob/main/PetShop/rabbit.jpg?raw=true', 2),
('Hamster 1', 200000, 1, '#FFCC00', 'A small hamster with lots of energy.', 'NOT_SALED', 20, 'https://github.com/vantuong147/ImageAssets/blob/main/PetShop/hamster.jpg?raw=true', 0.3),
('Hamster 2', 300000, 1, '#FFCC00', 'A hamster with golden fur, loves to run in the wheel.', 'NOT_SALED', 20, 'https://github.com/vantuong147/ImageAssets/blob/main/PetShop/hamster.jpg?raw=true', 0.3),
('Macaw Parrot 1', 18000000, 3, '#1D91C0', 'A blue parrot with a sharp beak, known for mimicking sounds.', 'NOT_SALED', 21, 'https://github.com/vantuong147/ImageAssets/blob/main/PetShop/parrot.jpg?raw=true', 1),
('Macaw Parrot 2', 22000000, 4, '#1D91C0', 'A colorful parrot that loves to interact with humans.', 'NOT_SALED', 21, 'https://github.com/vantuong147/ImageAssets/blob/main/PetShop/parrot.jpg?raw=true', 1),
('Goldfish 1', 200000, 1, '#FF4500', 'A goldfish with a beautiful orange color, loves swimming in tanks.', 'NOT_SALED', 22, 'https://github.com/vantuong147/ImageAssets/blob/main/PetShop/goldfish.jpg?raw=true', 0.1),
('Goldfish 2', 300000, 2, '#FF4500', 'A goldfish with long flowing fins, easy to care for.', 'NOT_SALED', 22, 'https://github.com/vantuong147/ImageAssets/blob/main/PetShop/goldfish.jpg?raw=true', 0.1),
('Guinea Pig 1', 600000, 2, '#8B4513', 'A guinea pig that enjoys socializing with other pets.', 'NOT_SALED', 23, 'https://github.com/vantuong147/ImageAssets/blob/main/PetShop/Guinea_Pig.jpg?raw=true', 1.5),
('Guinea Pig 2', 800000, 3, '#8B4513', 'A friendly guinea pig, loves to munch on vegetables.', 'NOT_SALED', 23, 'https://github.com/vantuong147/ImageAssets/blob/main/PetShop/Guinea_Pig.jpg?raw=true', 1.5),
('Turtle 1', 1500000, 4, '#228B22', 'A slow-moving turtle that enjoys the sun and water.', 'NOT_SALED', 24, 'https://github.com/vantuong147/ImageAssets/blob/main/PetShop/turtle.jpg?raw=true', 2),
('Turtle 2', 2000000, 6, '#228B22', 'A mature turtle with a sturdy shell, very calm.', 'NOT_SALED', 24, 'https://github.com/vantuong147/ImageAssets/blob/main/PetShop/turtle.jpg?raw=true', 2),
('Snake 1', 1000000, 2, '#2F4F4F', 'A non-venomous snake that enjoys climbing.', 'NOT_SALED', 25, 'https://github.com/vantuong147/ImageAssets/blob/main/PetShop/snake.jpg?raw=true', 1),
('Snake 2', 1500000, 3, '#2F4F4F', 'A snake with striking patterns, a true reptilian beauty.', 'NOT_SALED', 25, 'https://github.com/vantuong147/ImageAssets/blob/main/PetShop/snake.jpg?raw=true', 1),
('Frog 1', 150000, 1, '#228B22', 'A small frog, loves to leap across ponds.', 'NOT_SALED', 26, 'https://github.com/vantuong147/ImageAssets/blob/main/PetShop/frog.jpg?raw=true', 0.2),
('Frog 2', 250000, 2, '#228B22', 'A playful frog, always hopping around.', 'NOT_SALED', 26, 'https://github.com/vantuong147/ImageAssets/blob/main/PetShop/frog.jpg?raw=true', 0.2),
('Chinchilla 1', 5000000, 2, '#D3D3D3', 'A soft-furred chinchilla, loves to explore.', 'NOT_SALED', 27, 'https://github.com/vantuong147/ImageAssets/blob/main/PetShop/chinchilla.jpg?raw=true', 1),
('Chinchilla 2', 7000000, 3, '#D3D3D3', 'A chinchilla with a playful personality, loves running on wheels.', 'NOT_SALED', 27, 'https://github.com/vantuong147/ImageAssets/blob/main/PetShop/chinchilla.jpg?raw=true', 1),
('Ferret 1', 1500000, 1, '#C19A6B', 'A curious ferret, loves hiding and playing.', 'NOT_SALED', 28, 'https://github.com/vantuong147/ImageAssets/blob/main/PetShop/ferret.jpg?raw=true', 1),
('Ferret 2', 2000000, 2, '#C19A6B', 'A ferret that loves to interact with people and explore.', 'NOT_SALED', 28, 'https://github.com/vantuong147/ImageAssets/blob/main/PetShop/ferret.jpg?raw=true', 1);
