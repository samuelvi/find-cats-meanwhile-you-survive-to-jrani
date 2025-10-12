import pygame

class Bed(pygame.sprite.Sprite):
    def __init__(self, x, y):
        super().__init__()
        self.image = pygame.Surface([60, 80], pygame.SRCALPHA)
        # Bed color
        BED_COLOR = (139, 69, 19)  # Brown
        SHEET_COLOR = (255, 255, 255) # White
        PILLOW_COLOR = (240, 240, 240) # Light grey
        # Draw bed
        pygame.draw.rect(self.image, BED_COLOR, (0, 0, 60, 80))
        pygame.draw.rect(self.image, SHEET_COLOR, (5, 5, 50, 70))
        # Draw pillows
        pygame.draw.rect(self.image, PILLOW_COLOR, (10, 10, 20, 15))
        pygame.draw.rect(self.image, PILLOW_COLOR, (30, 10, 20, 15))
        self.rect = self.image.get_rect()
        self.rect.topleft = (x, y)

class Sofa(pygame.sprite.Sprite):
    def __init__(self, x, y):
        super().__init__()
        self.image = pygame.Surface([100, 50], pygame.SRCALPHA)
        # Sofa color
        SOFA_COLOR = (100, 100, 100) # Grey
        CUSHION_COLOR = (120, 120, 120) # Darker grey
        # Draw sofa
        pygame.draw.rect(self.image, SOFA_COLOR, (0, 0, 100, 50))
        # Draw cushions
        pygame.draw.rect(self.image, CUSHION_COLOR, (10, 10, 35, 30))
        pygame.draw.rect(self.image, CUSHION_COLOR, (55, 10, 35, 30))
        self.rect = self.image.get_rect()
        self.rect.topleft = (x, y)

class Drawer(pygame.sprite.Sprite):
    def __init__(self, x, y):
        super().__init__()
        self.image = pygame.Surface([40, 60], pygame.SRCALPHA)
        # Drawer color
        DRAWER_COLOR = (205, 133, 63) # Peru
        HANDLE_COLOR = (0, 0, 0) # Black
        # Draw drawer
        pygame.draw.rect(self.image, DRAWER_COLOR, (0, 0, 40, 60))
        pygame.draw.line(self.image, (0,0,0), (0, 20), (40, 20), 2)
        pygame.draw.line(self.image, (0,0,0), (0, 40), (40, 40), 2)
        # Draw handles
        pygame.draw.rect(self.image, HANDLE_COLOR, (15, 10, 10, 5))
        pygame.draw.rect(self.image, HANDLE_COLOR, (15, 30, 10, 5))
        pygame.draw.rect(self.image, HANDLE_COLOR, (15, 50, 10, 5))
        self.rect = self.image.get_rect()
        self.rect.topleft = (x, y)
