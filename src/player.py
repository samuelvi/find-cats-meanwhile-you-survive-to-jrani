import pygame

class Player(pygame.sprite.Sprite):
    def __init__(self, x, y):
        super().__init__()
        self.image = pygame.Surface([30, 30], pygame.SRCALPHA)
        
        # --- Colores ---
        SKIN_COLOR = (255, 224, 189)  # Tono de piel
        HAIR_COLOR = (110, 72, 39)    # Marrón
        SHIRT_COLOR = (50, 100, 200) # Azul
        BLACK = (0, 0, 0)

        # --- Dibujo ---
        # Camiseta
        pygame.draw.rect(self.image, SHIRT_COLOR, (5, 17, 20, 13))
        # Cabeza
        pygame.draw.circle(self.image, SKIN_COLOR, (15, 12), 8)
        # Pelo
        pygame.draw.ellipse(self.image, HAIR_COLOR, (8, 2, 14, 10))
        # Ojos
        pygame.draw.circle(self.image, BLACK, (12, 11), 1)
        pygame.draw.circle(self.image, BLACK, (18, 11), 1)
        # Sonrisa
        pygame.draw.arc(self.image, BLACK, (12, 13, 6, 4), 3.5, 0, 1)

        self.rect = self.image.get_rect()
        self.rect.topleft = (x, y)
        self.speed = 5

    def update(self, keys, walls):
        # Movimiento horizontal
        if keys[pygame.K_LEFT]:
            self.rect.x -= self.speed
        if keys[pygame.K_RIGHT]:
            self.rect.x += self.speed

        # Comprobación de colisiones horizontales
        for wall in walls:
            if self.rect.colliderect(wall):
                if keys[pygame.K_LEFT]:
                    self.rect.left = wall.right
                if keys[pygame.K_RIGHT]:
                    self.rect.right = wall.left

        # Movimiento vertical
        if keys[pygame.K_UP]:
            self.rect.y -= self.speed
        if keys[pygame.K_DOWN]:
            self.rect.y += self.speed

        # Comprobación de colisiones verticales
        for wall in walls:
            if self.rect.colliderect(wall):
                if keys[pygame.K_UP]:
                    self.rect.top = wall.bottom
                if keys[pygame.K_DOWN]:
                    self.rect.bottom = wall.top
