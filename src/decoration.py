import pygame
import random

class Painting(pygame.sprite.Sprite):
    def __init__(self, x, y):
        super().__init__()
        self.image = pygame.Surface([40, 60], pygame.SRCALPHA)
        
        # Frame color
        FRAME_COLOR = (139, 69, 19) # Brown
        # Painting color
        PAINTING_COLOR = (random.randint(100, 200), random.randint(100, 200), random.randint(100, 200))
        
        # Draw frame
        pygame.draw.rect(self.image, FRAME_COLOR, (0, 0, 40, 60))
        # Draw painting
        pygame.draw.rect(self.image, PAINTING_COLOR, (5, 5, 30, 50))
        
        self.rect = self.image.get_rect()
        self.rect.topleft = (x, y)

class Rug(pygame.sprite.Sprite):
    def __init__(self, x, y):
        super().__init__()
        self.image = pygame.Surface([100, 60], pygame.SRCALPHA)
        
        # Rug color
        RUG_COLOR = (random.randint(100, 150), random.randint(50, 100), random.randint(50, 100))
        
        # Draw rug
        pygame.draw.rect(self.image, RUG_COLOR, (0, 0, 100, 60))
        # Add some pattern
        pygame.draw.line(self.image, (255,255,255), (10, 10), (90, 50), 2)
        pygame.draw.line(self.image, (255,255,255), (10, 50), (90, 10), 2)

        self.rect = self.image.get_rect()
        self.rect.topleft = (x, y)

class Window(pygame.sprite.Sprite):
    def __init__(self, x, y):
        super().__init__()
        self.image = pygame.Surface([60, 80], pygame.SRCALPHA)
        
        # Frame color
        FRAME_COLOR = (200, 200, 200) # Light grey
        # Glass color
        GLASS_COLOR = (173, 216, 230) # Light blue
        
        # Draw frame
        pygame.draw.rect(self.image, FRAME_COLOR, (0, 0, 60, 80))
        # Draw glass
        pygame.draw.rect(self.image, GLASS_COLOR, (5, 5, 50, 70))
        # Draw window panes
        pygame.draw.line(self.image, FRAME_COLOR, (30, 5), (30, 75), 2)
        pygame.draw.line(self.image, FRAME_COLOR, (5, 40), (55, 40), 2)

        self.rect = self.image.get_rect()
        self.rect.topleft = (x, y)
