import pygame
import math

class Enemy(pygame.sprite.Sprite):
    def __init__(self, x, y, sound_manager):
        super().__init__()
        self.image = pygame.Surface([35, 35], pygame.SRCALPHA)
        
        # --- Colores ---
        SKIN_COLOR = (240, 218, 200)
        HAIR_COLOR = (200, 200, 200)
        DRESS_COLOR = (88, 41, 92)
        BLACK = (0, 0, 0)

        # --- Dibujo ---
        # Vestido (cuerpo)
        pygame.draw.rect(self.image, DRESS_COLOR, (5, 18, 25, 17))
        # Cabeza
        pygame.draw.circle(self.image, SKIN_COLOR, (17, 14), 10)
        # Pelo (moño)
        pygame.draw.circle(self.image, HAIR_COLOR, (17, 5), 6)
        # Ojos enfadados
        pygame.draw.line(self.image, BLACK, (11, 11), (16, 14), 2)
        pygame.draw.line(self.image, BLACK, (23, 11), (18, 14), 2)
        # Boca (fruncida)
        pygame.draw.arc(self.image, BLACK, (12, 18, 10, 6), 3.14, 0, 2)

        self.rect = self.image.get_rect()
        self.rect.topleft = (x, y)
        self.speed = 2 # Jrani es más lenta que el jugador... por ahora
        self.is_chasing = False
        self.sound_manager = sound_manager
        self.say_i_see_you_timer = 0

    def update(self, player, walls):
        # Lógica de persecución simple
        dx = player.rect.x - self.rect.x
        dy = player.rect.y - self.rect.y
        dist = math.hypot(dx, dy)

        # Si el jugador está cerca, lo persigue
        if dist < 250: # Campo de visión
            self.is_chasing = True
            # Moverse hacia el jugador
            if dist > 0:
                dx, dy = dx / dist, dy / dist
            
            self.rect.x += dx * self.speed
            self.handle_collision(dx, 0, walls)
            self.rect.y += dy * self.speed
            self.handle_collision(0, dy, walls)

            # Decir "I see you" de vez en cuando
            self.say_i_see_you_timer += 1
            if self.say_i_see_you_timer > 180: # 3 segundos a 60 FPS
                self.sound_manager.play_sfx('i_see_you')
                self.say_i_see_you_timer = 0
        else:
            self.is_chasing = False

    def handle_collision(self, dx, dy, walls):
        # Similar al jugador, pero para el enemigo
        for wall in walls:
            if self.rect.colliderect(wall):
                if dx > 0: # Moviéndose a la derecha
                    self.rect.right = wall.left
                if dx < 0: # Moviéndose a la izquierda
                    self.rect.left = wall.right
                if dy > 0: # Moviéndose hacia abajo
                    self.rect.bottom = wall.top
                if dy < 0: # Moviéndose hacia arriba
                    self.rect.top = wall.bottom
