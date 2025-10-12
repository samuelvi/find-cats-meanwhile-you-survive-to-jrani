import pygame

def create_wall_texture(filename="assets/wall_texture.png", width=30, height=15):
    surface = pygame.Surface((width, height))
    surface.fill((100, 80, 70)) # Base brick color
    pygame.draw.rect(surface, (50, 40, 35), (0, 0, width, height), 1) # Brick outline
    pygame.image.save(surface, filename)
    print(f"Created {filename}")

def create_floor_texture(filename="assets/floor_texture.png", width=40, height=40):
    surface = pygame.Surface((width, height))
    surface.fill((84, 55, 30)) # Dark wood color
    pygame.draw.line(surface, (44, 25, 10), (0, 0), (width, height), 1) # Wood grain
    pygame.draw.line(surface, (44, 25, 10), (width, 0), (0, height), 1) # Wood grain
    pygame.image.save(surface, filename)
    print(f"Created {filename}")

if __name__ == "__main__":
    pygame.init() # Initialize pygame for image saving
    create_wall_texture()
    create_floor_texture()
    pygame.quit()