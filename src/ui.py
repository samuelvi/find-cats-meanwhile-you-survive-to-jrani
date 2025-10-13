import pygame

class Button:
    def __init__(self, x, y, width, height, text, color, hover_color):
        self.rect = pygame.Rect(x, y, width, height)
        self.text = text
        self.color = color
        self.hover_color = hover_color
        self.font = pygame.font.Font(None, 40)

    def draw(self, surface, mouse_pos=None):
        if mouse_pos is None:
            mouse_pos = pygame.mouse.get_pos()
        is_hovered = self.rect.collidepoint(mouse_pos)
        color = self.hover_color if is_hovered else self.color
        pygame.draw.rect(surface, color, self.rect)
        
        text_surf = self.font.render(self.text, True, (255, 255, 255))
        text_rect = text_surf.get_rect(center=self.rect.center)
        surface.blit(text_surf, text_rect)

    def is_clicked(self, event, mouse_pos=None):
        if mouse_pos is None:
            mouse_pos = event.pos
        return event.type == pygame.MOUSEBUTTONDOWN and self.rect.collidepoint(mouse_pos)
