import pygame
import random

def create_wall_texture(filename="assets/wall_texture.png"):
    surface = pygame.Surface((800, 600), pygame.SRCALPHA)
    # Light grey base
    surface.fill((200, 200, 200))
    # Subtle noise for texture
    for _ in range(2000):
        x = random.randint(0, 800)
        y = random.randint(0, 600)
        color = random.randint(190, 210)
        surface.set_at((x, y), (color, color, color))
    pygame.image.save(surface, filename)
    print(f"Created {filename}")

def create_floor_texture(filename="assets/floor_texture.png"):
    surface = pygame.Surface((800, 600), pygame.SRCALPHA)
    # Wooden floor color
    surface.fill((139, 69, 19))
    # Planks
    for y in range(0, 600, 20):
        pygame.draw.line(surface, (101, 57, 19), (0, y), (800, y), 1)
    # Wood grain
    for _ in range(1000):
        x = random.randint(0, 800)
        y = random.randint(0, 600)
        color = random.randint(120, 150)
        surface.set_at((x, y), (color, color - 20, color - 40))
    pygame.image.save(surface, filename)
    print(f"Created {filename}")

if __name__ == "__main__":
    pygame.init()
    create_wall_texture()
    create_floor_texture()
    pygame.quit()