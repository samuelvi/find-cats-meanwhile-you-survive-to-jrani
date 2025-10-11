import pygame

class Cat(pygame.sprite.Sprite):
    def __init__(self, x, y):
        super().__init__()
        # Crear una superficie de 24x24 con transparencia
        self.image = pygame.Surface([24, 24], pygame.SRCALPHA)
        
        CAT_COLOR = pygame.Color('orange')
        EYE_COLOR = pygame.Color('black')

        # Dibujar la cabeza (círculo)
        pygame.draw.circle(self.image, CAT_COLOR, (12, 14), 10)

        # Dibujar las orejas (polígonos/triángulos)
        pygame.draw.polygon(self.image, CAT_COLOR, [(1, 0), (8, 8), (1, 8)])
        pygame.draw.polygon(self.image, CAT_COLOR, [(23, 0), (16, 8), (23, 8)])

        # Dibujar los ojos (círculos pequeños)
        pygame.draw.circle(self.image, EYE_COLOR, (8, 12), 2)
        pygame.draw.circle(self.image, EYE_COLOR, (16, 12), 2)

        self.rect = self.image.get_rect()
        self.rect.topleft = (x, y)

class Key(pygame.sprite.Sprite):
    def __init__(self, x, y):
        super().__init__()
        self.image = pygame.Surface([30, 30], pygame.SRCALPHA)
        KEY_COLOR = (255, 215, 0)  # Color oro
        SHADOW_COLOR = (200, 170, 0) # Sombra

        # Cabeza de la llave
        pygame.draw.circle(self.image, KEY_COLOR, (15, 8), 8)
        pygame.draw.circle(self.image, SHADOW_COLOR, (15, 8), 8, 2)
        # Cuerpo de la llave
        pygame.draw.rect(self.image, KEY_COLOR, (13, 15, 4, 15))
        # Diente de la llave
        pygame.draw.rect(self.image, KEY_COLOR, (8, 24, 10, 4))

        self.rect = self.image.get_rect()
        self.rect.topleft = (x, y)

class Platform(pygame.sprite.Sprite):
    def __init__(self, x, y):
        super().__init__()
        self.image = pygame.Surface([80, 80], pygame.SRCALPHA)
        
        BASE_COLOR = (245, 245, 220) # Un blanco hueso
        SHADOW_COLOR = (210, 210, 180)
        DARK_SHADOW = (180, 180, 150)

        # Borde superior del cesto (elipse)
        pygame.draw.ellipse(self.image, DARK_SHADOW, (10, 20, 60, 25), 4)
        
        # Cuerpo del cesto
        body_rect = pygame.Rect(10, 32, 60, 40)
        pygame.draw.rect(self.image, BASE_COLOR, body_rect)

        # Líneas verticales para textura
        for i in range(1, 5):
            x_pos = 10 + i * 12
            pygame.draw.line(self.image, SHADOW_COLOR, (x_pos, 32), (x_pos, 72), 1)

        # Borde inferior
        pygame.draw.arc(self.image, DARK_SHADOW, (10, 62, 60, 20), 3.14, 0, 4)

        self.rect = self.image.get_rect()
        self.rect.topleft = (x, y)

class Stair(pygame.sprite.Sprite):
    def __init__(self, x, y, goes_to_floor, target_pos, direction='down'):
        super().__init__()
        self.image = pygame.Surface([50, 50], pygame.SRCALPHA)
        
        self.goes_to_floor = goes_to_floor
        self.target_pos = target_pos
        self.rect = self.image.get_rect(topleft=(x, y))

        # Dibuja la escalera según la dirección
        if direction == 'down':
            # Simula un hueco oscuro con peldaños de madera
            pygame.draw.rect(self.image, (30, 20, 10), (0, 0, 50, 50))
            for i in range(6):
                pygame.draw.line(self.image, (84, 55, 30), (0, i * 5), (50, i * 5), 2)
        else: # 'up'
            # Simula escalones de piedra que suben
            pygame.draw.rect(self.image, (110, 110, 110), (0, 0, 50, 50))
            for i in range(6):
                pygame.draw.line(self.image, (150, 150, 150), (0, 50 - i * 5), (50, 50 - i * 5), 2)

class Door(pygame.sprite.Sprite):
    def __init__(self, x, y, width, height):
        super().__init__()
        self.image = pygame.Surface([width, height])
        self.image.fill((112, 77, 48)) # Color madera oscura para la puerta
        self.rect = self.image.get_rect()
        self.rect.topleft = (x, y)