
import pygame

def create_bed_asset(filename="assets/bed.png"):
    surface = pygame.Surface((60, 80), pygame.SRCALPHA)
    # Bed frame
    pygame.draw.rect(surface, (139, 69, 19), (0, 0, 60, 80))
    # Mattress
    pygame.draw.rect(surface, (255, 255, 255), (5, 5, 50, 70))
    # Pillow
    pygame.draw.rect(surface, (220, 220, 220), (10, 10, 40, 20))
    pygame.draw.rect(surface, (200, 200, 200), (10, 10, 40, 20), 1)
    # Duvet
    pygame.draw.rect(surface, (173, 216, 230), (5, 35, 50, 40))
    pygame.draw.line(surface, (135, 206, 250), (5, 55), (55, 55), 2)
    pygame.image.save(surface, filename)
    print(f"Created {filename}")

def create_sofa_asset(filename="assets/sofa.png"):
    surface = pygame.Surface((100, 50), pygame.SRCALPHA)
    # Sofa base
    pygame.draw.rect(surface, (100, 100, 100), (0, 10, 100, 40))
    # Back
    pygame.draw.rect(surface, (120, 120, 120), (0, 0, 100, 20))
    # Cushions
    pygame.draw.rect(surface, (140, 140, 140), (10, 20, 35, 20))
    pygame.draw.rect(surface, (140, 140, 140), (55, 20, 35, 20))
    pygame.image.save(surface, filename)
    print(f"Created {filename}")

def create_drawer_asset(filename="assets/drawer.png"):
    surface = pygame.Surface((40, 60), pygame.SRCALPHA)
    # Cabinet
    pygame.draw.rect(surface, (205, 133, 63), (0, 0, 40, 60))
    # Drawers
    pygame.draw.rect(surface, (184, 115, 51), (5, 5, 30, 15))
    pygame.draw.rect(surface, (184, 115, 51), (5, 25, 30, 15))
    pygame.draw.rect(surface, (184, 115, 51), (5, 45, 30, 10))
    # Handles
    pygame.draw.rect(surface, (0, 0, 0), (18, 10, 4, 4))
    pygame.draw.rect(surface, (0, 0, 0), (18, 30, 4, 4))
    pygame.draw.rect(surface, (0, 0, 0), (18, 50, 4, 4))
    pygame.image.save(surface, filename)
    print(f"Created {filename}")

def create_painting_asset(filename="assets/painting.png"):
    surface = pygame.Surface((40, 60), pygame.SRCALPHA)
    # Frame
    pygame.draw.rect(surface, (139, 69, 19), (0, 0, 40, 60))
    # Canvas
    pygame.draw.rect(surface, (255, 250, 240), (5, 5, 30, 50))
    # Abstract art
    pygame.draw.line(surface, (255, 0, 0), (10, 10), (30, 50), 2)
    pygame.draw.line(surface, (0, 0, 255), (10, 50), (30, 10), 2)
    pygame.draw.circle(surface, (255, 255, 0), (20, 30), 5)
    pygame.image.save(surface, filename)
    print(f"Created {filename}")

def create_rug_asset(filename="assets/rug.png"):
    surface = pygame.Surface((100, 60), pygame.SRCALPHA)
    # Base
    pygame.draw.rect(surface, (150, 111, 214), (0, 0, 100, 60))
    # Pattern
    pygame.draw.rect(surface, (128, 0, 128), (10, 10, 80, 40))
    pygame.draw.circle(surface, (255, 215, 0), (50, 30), 15)
    pygame.image.save(surface, filename)
    print(f"Created {filename}")

def create_window_asset(filename="assets/window.png"):
    surface = pygame.Surface((60, 80), pygame.SRCALPHA)
    # Frame
    pygame.draw.rect(surface, (200, 200, 200), (0, 0, 60, 80))
    # Glass
    pygame.draw.rect(surface, (173, 216, 230), (5, 5, 50, 70))
    # Panes
    pygame.draw.line(surface, (200, 200, 200), (30, 5), (30, 75), 2)
    pygame.draw.line(surface, (200, 200, 200), (5, 40), (55, 40), 2)
    # Night sky
    pygame.draw.rect(surface, (0, 0, 20), (10, 10, 20, 20))
    pygame.draw.circle(surface, (255, 255, 255), (15, 15), 2)
    pygame.draw.circle(surface, (255, 255, 255), (25, 20), 1)
    pygame.image.save(surface, filename)
    print(f"Created {filename}")

def create_chair_asset(filename="assets/chair.png"):
    surface = pygame.Surface((30, 50), pygame.SRCALPHA)
    # Back
    pygame.draw.rect(surface, (139, 69, 19), (0, 0, 30, 10))
    # Seat
    pygame.draw.rect(surface, (160, 82, 45), (0, 10, 30, 10))
    # Legs
    pygame.draw.rect(surface, (139, 69, 19), (0, 20, 5, 30))
    pygame.draw.rect(surface, (139, 69, 19), (25, 20, 5, 30))
    pygame.image.save(surface, filename)
    print(f"Created {filename}")

def create_table_asset(filename="assets/table.png"):
    surface = pygame.Surface((80, 40), pygame.SRCALPHA)
    # Tabletop
    pygame.draw.rect(surface, (139, 69, 19), (0, 0, 80, 10))
    # Legs
    pygame.draw.rect(surface, (160, 82, 45), (5, 10, 10, 30))
    pygame.draw.rect(surface, (160, 82, 45), (65, 10, 10, 30))
    pygame.image.save(surface, filename)
    print(f"Created {filename}")

def create_plant_asset(filename="assets/plant.png"):
    surface = pygame.Surface((30, 50), pygame.SRCALPHA)
    # Pot
    pygame.draw.rect(surface, (210, 105, 30), (5, 30, 20, 20))
    # Plant
    pygame.draw.rect(surface, (0, 128, 0), (10, 10, 10, 20))
    pygame.draw.circle(surface, (34, 139, 34), (15, 10), 10)
    pygame.image.save(surface, filename)
    print(f"Created {filename}")

def create_lamp_asset(filename="assets/lamp.png"):
    surface = pygame.Surface((20, 50), pygame.SRCALPHA)
    # Base
    pygame.draw.rect(surface, (105, 105, 105), (5, 40, 10, 10))
    # Stand
    pygame.draw.rect(surface, (169, 169, 169), (8, 10, 4, 30))
    # Lampshade
    pygame.draw.polygon(surface, (255, 255, 0), [(0, 0), (20, 0), (15, 10), (5, 10)])
    pygame.image.save(surface, filename)
    print(f"Created {filename}")

if __name__ == "__main__":
    pygame.init()
    create_bed_asset()
    create_sofa_asset()
    create_drawer_asset()
    create_painting_asset()
    create_rug_asset()
    create_window_asset()
    create_chair_asset()
    create_table_asset()
    create_plant_asset()
    create_lamp_asset()
    pygame.quit()
