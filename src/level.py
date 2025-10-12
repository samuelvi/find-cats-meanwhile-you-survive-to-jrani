import pygame

# --- Diseños de los Pisos (con huecos para puertas) ---
LEVEL_1_WALLS = [
    (0, 0, 800, 10), (0, 590, 800, 10), (0, 0, 10, 600), (790, 0, 10, 600),
    # Pared vertical con puerta
    (100, 100, 10, 150), (100, 290, 10, 210),
    # Pared horizontal
    (250, 150, 400, 10)
]

LEVEL_2_WALLS = [
    (0, 0, 800, 10), (0, 590, 800, 10), (0, 0, 10, 600), (790, 0, 10, 600),
    (400, 0, 10, 250), (400, 350, 10, 250),
    # Pared horizontal con puerta
    (150, 300, 200, 10), (400, 300, 250, 10)
]

LEVEL_3_WALLS = [
    (0, 0, 800, 10), (0, 590, 800, 10), (0, 0, 10, 600), (790, 0, 10, 600),
    # Pared superior con puerta
    (100, 100, 280, 10), (420, 100, 280, 10),
    # Pared inferior
    (100, 500, 600, 10)
]

class Level:
    def __init__(self, wall_layout):
        self.walls = []
        for wall_pos in wall_layout:
            self.walls.append(pygame.Rect(wall_pos))
        self.wall_texture = pygame.image.load("assets/wall_texture.png").convert_alpha()

    def draw(self, surface):
        WALL_HIGHLIGHT = (130, 110, 100)   # Borde superior iluminado
        WALL_SHADOW = (50, 40, 35)         # Sombra inferior

        for wall in self.walls:
            # Blit the wall texture
            for x in range(wall.left, wall.right, self.wall_texture.get_width()):
                for y in range(wall.top, wall.bottom, self.wall_texture.get_height()):
                    surface.blit(self.wall_texture, (x, y))

            # Efecto 3D con bordes claros y oscuros
            pygame.draw.line(surface, WALL_HIGHLIGHT, wall.topleft, wall.topright, 2)
            pygame.draw.line(surface, WALL_HIGHLIGHT, wall.topleft, wall.bottomleft, 2)
            pygame.draw.line(surface, WALL_SHADOW, wall.bottomleft, wall.bottomright, 3)
            pygame.draw.line(surface, WALL_SHADOW, wall.topright, wall.bottomright, 3)