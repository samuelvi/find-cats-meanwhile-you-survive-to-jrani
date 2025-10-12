import pygame

class Bed(pygame.sprite.Sprite):
    def __init__(self, x, y):
        super().__init__()
        self.image = pygame.Surface([60, 80], pygame.SRCALPHA)
        # Bed color
        BED_COLOR = (139, 69, 19)  # Brown
        SHEET_COLOR = (255, 255, 255) # White
        # Draw bed
        pygame.draw.rect(self.image, BED_COLOR, (0, 0, 60, 80))
        pygame.draw.rect(self.image, SHEET_COLOR, (5, 5, 50, 60))
        self.rect = self.image.get_rect()
        self.rect.topleft = (x, y)

class Sofa(pygame.sprite.Sprite):
    def __init__(self, x, y):
        super().__init__()
        self.image = pygame.Surface([100, 50], pygame.SRCALPHA)
        # Sofa color
        SOFA_COLOR = (100, 100, 100) # Grey
        # Draw sofa
        pygame.draw.rect(self.image, SOFA_COLOR, (0, 0, 100, 50))
        self.rect = self.image.get_rect()
        self.rect.topleft = (x, y)

class Drawer(pygame.sprite.Sprite):
    def __init__(self, x, y):
        super().__init__()
        self.image = pygame.Surface([40, 60], pygame.SRCALPHA)
        # Drawer color
        DRAWER_COLOR = (205, 133, 63) # Peru
        # Draw drawer
        pygame.draw.rect(self.image, DRAWER_COLOR, (0, 0, 40, 60))
        pygame.draw.line(self.image, (0,0,0), (0, 20), (40, 20), 2)
        pygame.draw.line(self.image, (0,0,0), (0, 40), (40, 40), 2)
        self.rect = self.image.get_rect()
        self.rect.topleft = (x, y)
