import pygame
import sys
import random
from src.player import Player
from src.level import Level, LEVEL_1_WALLS, LEVEL_2_WALLS, LEVEL_3_WALLS
from src.items import Cat, Key, Platform, Stair, Door
from src.enemy import Enemy
from src.ui import Button
from src.sound_manager import SoundManager

# --- Constantes ---
SCREEN_WIDTH = 800
SCREEN_HEIGHT = 600
BACKGROUND_COLOR = (0, 0, 0)

# --- Dificultades ---
DIFFICULTY_SETTINGS = {
    'Fácil': {'lives': 8, 'jrani_speed': 2},
    'Normal': {'lives': 5, 'jrani_speed': 3},
    'Difícil': {'lives': 3, 'jrani_speed': 4}
}

class Game:
    def __init__(self, difficulty): # Acepta la dificultad
        print("Game: __init__ started")
        pygame.init()
        self.screen = pygame.display.set_mode((SCREEN_WIDTH, SCREEN_HEIGHT))
        pygame.display.set_caption("Find Cats and Survive Jrani")
        self.clock = pygame.time.Clock()
        self.font = pygame.font.Font(None, 36)
        self.running = True
        self.fullscreen = False

        self.sound_manager = SoundManager()

        settings = DIFFICULTY_SETTINGS[difficulty]
        self.player = Player(SCREEN_WIDTH // 2, SCREEN_HEIGHT // 2)
        self.player_lives = settings['lives']
        self.jrani_speed = settings['jrani_speed']
        
        self.levels = {
            1: Level(LEVEL_1_WALLS),
            2: Level(LEVEL_2_WALLS),
            3: Level(LEVEL_3_WALLS)
        }
        self.current_floor = 1

        self.floor_surface = self._create_floor_surface()
        self.setup_floor_objects()
        print("Game: __init__ finished")

    def _create_floor_surface(self):
        """Crea una superficie que simula un suelo de madera."""
        floor = pygame.Surface((SCREEN_WIDTH, SCREEN_HEIGHT))
        # Color base oscuro
        floor.fill((54, 35, 20))
        
        PLANK_WIDTH = 40
        plank_colors = [(84, 55, 30), (94, 65, 40)]
        
        for x in range(0, SCREEN_WIDTH, PLANK_WIDTH):
            # Elige un tono para el tablón
            color = plank_colors[(x // PLANK_WIDTH) % 2]
            plank_rect = pygame.Rect(x, 0, PLANK_WIDTH, SCREEN_HEIGHT)
            pygame.draw.rect(floor, color, plank_rect)
            
            # Dibuja una línea oscura para separar los tablones
            if x > 0:
                pygame.draw.line(floor, (44, 25, 10), (x, 0), (x, SCREEN_HEIGHT), 1)
        return floor

    def _find_random_valid_spot(self, floor_num, sprite_size=(24, 24)):
        """Encuentra un punto de aparición aleatorio y válido en un piso determinado."""
        walls = self.levels[floor_num].walls
        while True:
            # Genera una posición aleatoria con un margen respecto a los bordes
            x = random.randint(20, SCREEN_WIDTH - 20 - sprite_size[0])
            y = random.randint(20, SCREEN_HEIGHT - 20 - sprite_size[1])
            
            potential_rect = pygame.Rect(x, y, sprite_size[0], sprite_size[1])
            
            # Si no choca con ninguna pared, es un punto válido
            if not any(potential_rect.colliderect(wall) for wall in walls):
                return (x, y)

    def setup_floor_objects(self):
        print("Game: setup_floor_objects started")
        """Configura los objetos para todos los pisos."""
        self.all_sprites = pygame.sprite.Group()
        self.all_sprites.add(self.player)

        # Inicializar explícitamente todos los diccionarios _by_floor
        self.stairs_by_floor = {}
        self.cats_by_floor = {}
        self.enemies_by_floor = {}
        self.doors_by_floor = {}

        # Escaleras por piso, indicando la dirección
        self.stairs_by_floor = {
            1: [Stair(720, 280, goes_to_floor=2, target_pos=(101, 280), direction='up')],
            2: [Stair(50, 280, goes_to_floor=1, target_pos=(689, 280), direction='down'),
                Stair(720, 520, goes_to_floor=3, target_pos=(101, 30), direction='up')],
            3: [Stair(50, 30, goes_to_floor=2, target_pos=(689, 520), direction='down')]
        }
        
        # Plataforma (solo en el primer piso)
        self.platform = Platform(360, 250)
        self.platform_group = pygame.sprite.Group(self.platform)

        # Llave (inicialmente vacía)
        self.key_group = pygame.sprite.Group()
        self.key_spawned = False
        self.is_chase_music_playing = False

        # --- Random Cat Placement ---
        self.cats_by_floor = {1: [], 2: [], 3: []}
        cat_distribution = {1: 2, 2: 2, 3: 1} # 2 gatos en piso 1, 2 en piso 2, 1 en piso 3

        for floor, num_cats in cat_distribution.items():
            for _ in range(num_cats):
                pos = self._find_random_valid_spot(floor)
                self.cats_by_floor[floor].append(Cat(pos[0], pos[1]))

        self.total_cats = sum(len(cats) for cats in self.cats_by_floor.values())
        self.cats_collected = 0

        # Jrani por piso
        self.enemies_by_floor = {
            1: [Enemy(100, 100)],
            2: [],
            3: [Enemy(700, 100)]
        }
        print(f"Enemies by floor AFTER POPULATION: {self.enemies_by_floor}") # Debug print
        # Aplicar velocidad de Jrani
        for floor_enemies in self.enemies_by_floor.values():
            for enemy in floor_enemies:
                enemy.speed = self.jrani_speed
        print(f"Enemies by floor: {self.enemies_by_floor}")

        self.load_current_floor_sprites() # Ahora se llama al final
        print("Game: setup_floor_objects finished")

    def load_current_floor_sprites(self):
        print("Game: load_current_floor_sprites started")
        """Limpia y carga los sprites para el piso actual."""
        # No eliminar al jugador
        self.all_sprites.empty()
        self.all_sprites.add(self.player)

        # Añadir objetos del piso actual
        self.cats = pygame.sprite.Group(*self.cats_by_floor.get(self.current_floor, []))
        self.enemies = pygame.sprite.Group(*self.enemies_by_floor.get(self.current_floor, []))
        self.stairs = pygame.sprite.Group(*self.stairs_by_floor.get(self.current_floor, []))
        self.doors = pygame.sprite.Group(*self.doors_by_floor.get(self.current_floor, []))

        self.all_sprites.add(self.cats, self.enemies, self.stairs, self.doors)
        
        if self.current_floor == 1:
            self.all_sprites.add(self.platform)
        
        # Añadir la llave si ya ha aparecido
        if self.key_spawned:
            self.all_sprites.add(self.key_group)
        print("Game: load_current_floor_sprites finished")

    def change_floor(self, new_floor, player_pos):
        """Cambia de piso y actualiza los sprites."""
        print(f"Cambiando al piso {new_floor}...")
        self.current_floor = new_floor
        self.player.rect.topleft = player_pos
        self.load_current_floor_sprites()

    def run(self):
        print("Game: run started")
        self.sound_manager.play_background_music()
        while self.running:
            print("Game: run loop entered")
            self.events()
            self.update()
            self.draw()
            self.clock.tick(60)
        
        pygame.mixer.music.stop()

    def events(self):
        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                self.running = False
            if event.type == pygame.KEYDOWN:
                if event.key == pygame.K_F11:
                    self.fullscreen = not self.fullscreen
                    if self.fullscreen:
                        self.screen = pygame.display.set_mode((SCREEN_WIDTH, SCREEN_HEIGHT), pygame.FULLSCREEN)
                    else:
                        self.screen = pygame.display.set_mode((SCREEN_WIDTH, SCREEN_HEIGHT))

    def update(self):
        current_level_walls = self.levels[self.current_floor].walls
        keys = pygame.key.get_pressed()
        
        self.player.update(keys, current_level_walls)
        self.enemies.update(self.player, current_level_walls)

        # Lógica de música de persecución
        is_any_enemy_chasing = any(e.is_chasing for e in self.enemies)
        if is_any_enemy_chasing and not self.is_chase_music_playing:
            self.sound_manager.play_chase_music()
            self.is_chase_music_playing = True
        elif not is_any_enemy_chasing and self.is_chase_music_playing:
            self.sound_manager.stop_chase_music()
            self.is_chase_music_playing = False

        # Colisión con gatos
        collected = pygame.sprite.spritecollide(self.player, self.cats, True)
        if collected:
            self.sound_manager.play_sfx('collect_cat')
            self.cats_collected += len(collected)
            self.cats_by_floor[self.current_floor] = [cat for cat in self.cats_by_floor[self.current_floor] if cat not in collected]
            print(f"Gatos recogidos: {self.cats_collected}/{self.total_cats}")

        # Colisión con Jrani
        if pygame.sprite.spritecollide(self.player, self.enemies, False):
            self.sound_manager.play_sfx('get_caught')
            self.player_lives -= 1
            print(f"¡Jrani te ha atrapado! Vidas restantes: {self.player_lives}")
            self.player.rect.topleft = (SCREEN_WIDTH // 2, SCREEN_HEIGHT // 2)
            if self.player_lives <= 0:
                print("Game Over, chaval.")
                self.running = False
            pygame.time.wait(500)

        # Colisión con escaleras
        stair_hit = pygame.sprite.spritecollideany(self.player, self.stairs)
        if stair_hit:
            self.change_floor(stair_hit.goes_to_floor, stair_hit.target_pos)
            pygame.time.wait(200)

        # Lógica de victoria
        if self.cats_collected == self.total_cats and not self.key_spawned and self.current_floor == 1:
            if pygame.sprite.spritecollide(self.player, self.platform_group, False):
                self.sound_manager.play_sfx('key_spawn')
                print("¡Has llevado todos los gatos! Aparece la llave.")
                key = Key(400, 50)
                self.key_group.add(key)
                self.all_sprites.add(key)
                self.key_spawned = True

        if self.key_spawned and pygame.sprite.spritecollide(self.player, self.key_group, True):
            self.sound_manager.play_sfx('win')
            print("¡Has escapado! ¡Enhorabuena!")
            self.running = False
            pygame.time.wait(1000)

    def draw(self):
        self.screen.blit(self.floor_surface, (0, 0))
        self.levels[self.current_floor].draw(self.screen)
        self.all_sprites.draw(self.screen)

        # UI
        lives_text = self.font.render(f'Vidas: {self.player_lives}', True, (255, 255, 255))
        cats_text = self.font.render(f'Gatos: {self.cats_collected}/{self.total_cats}', True, (255, 255, 255))
        floor_text = self.font.render(f'Piso: {self.current_floor}', True, (255, 255, 255))
        
        self.screen.blit(lives_text, (15, 15))
        self.screen.blit(cats_text, (15, 50))
        self.screen.blit(floor_text, (SCREEN_WIDTH - 100, 15))

        pygame.display.flip()

def main_menu():
    pygame.init() # Inicializar Pygame aquí
    screen = pygame.display.set_mode((SCREEN_WIDTH, SCREEN_HEIGHT))
    pygame.display.set_caption("Menú Principal")
    clock = pygame.time.Clock()
    font = pygame.font.Font(None, 74)

    title_text = font.render("Find Cats and Survive Jrani", True, (255, 255, 255))
    title_rect = title_text.get_rect(center=(SCREEN_WIDTH / 2, 150))

    buttons = [
        Button(300, 250, 200, 50, 'Fácil', (34, 139, 34), (46, 189, 46)),
        Button(300, 320, 200, 50, 'Normal', (255, 165, 0), (255, 195, 50)),
        Button(300, 390, 200, 50, 'Difícil', (178, 34, 34), (205, 50, 50))
    ]

    menu_running = True
    while menu_running:
        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                pygame.quit()
                sys.exit()
            for button in buttons:
                if button.is_clicked(event):
                    game = Game(difficulty=button.text)
                    game.run()
                    # Cuando el juego termina, volvemos al menú
                    # (o podríamos salir, pero volver al menú es más amigable)
                    # Para salir, simplemente quita el bucle y la llamada a game.run()

        screen.fill(BACKGROUND_COLOR)
        screen.blit(title_text, title_rect)
        for button in buttons:
            button.draw(screen)
        
        pygame.display.flip()
        clock.tick(60)

if __name__ == "__main__":
    main_menu()