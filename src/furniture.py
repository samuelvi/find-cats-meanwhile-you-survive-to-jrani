import pygame

class Bed(pygame.sprite.Sprite):
    def __init__(self, x, y):
        super().__init__()
        self.image = pygame.image.load("assets/bed.png").convert_alpha()
        self.rect = self.image.get_rect()
        self.rect.topleft = (x, y)

class Sofa(pygame.sprite.Sprite):
    def __init__(self, x, y):
        super().__init__()
        self.image = pygame.image.load("assets/sofa.png").convert_alpha()
        self.rect = self.image.get_rect()
        self.rect.topleft = (x, y)

class Drawer(pygame.sprite.Sprite):
    def __init__(self, x, y):
        super().__init__()
        self.image = pygame.image.load("assets/drawer.png").convert_alpha()
        self.rect = self.image.get_rect()
        self.rect.topleft = (x, y)

class Chair(pygame.sprite.Sprite):
    def __init__(self, x, y):
        super().__init__()
        self.image = pygame.image.load("assets/chair.png").convert_alpha()
        self.rect = self.image.get_rect()
        self.rect.topleft = (x, y)

class Table(pygame.sprite.Sprite):
    def __init__(self, x, y):
        super().__init__()
        self.image = pygame.image.load("assets/table.png").convert_alpha()
        self.rect = self.image.get_rect()
        self.rect.topleft = (x, y)

class Lamp(pygame.sprite.Sprite):
    def __init__(self, x, y):
        super().__init__()
        self.image = pygame.image.load("assets/lamp.png").convert_alpha()
        self.rect = self.image.get_rect()
        self.rect.topleft = (x, y)
