import pygame
import random

class Painting(pygame.sprite.Sprite):
    def __init__(self, x, y):
        super().__init__()
        self.image = pygame.image.load("assets/painting.png").convert_alpha()
        self.rect = self.image.get_rect()
        self.rect.topleft = (x, y)

class Rug(pygame.sprite.Sprite):
    def __init__(self, x, y):
        super().__init__()
        self.image = pygame.image.load("assets/rug.png").convert_alpha()
        self.rect = self.image.get_rect()
        self.rect.topleft = (x, y)

class Window(pygame.sprite.Sprite):
    def __init__(self, x, y):
        super().__init__()
        self.image = pygame.image.load("assets/window.png").convert_alpha()
        self.rect = self.image.get_rect()
        self.rect.topleft = (x, y)

class Plant(pygame.sprite.Sprite):
    def __init__(self, x, y):
        super().__init__()
        self.image = pygame.image.load("assets/plant.png").convert_alpha()
        self.rect = self.image.get_rect()
        self.rect.topleft = (x, y)
