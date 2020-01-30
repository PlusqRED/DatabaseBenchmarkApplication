INSERT INTO public.friend (id, age, name, surnmae) VALUES (1, 20, 'Oleg', 'Vinograd');
INSERT INTO public.post (id, content, title, friend_id) VALUES (1, 'hello', 'whole world', 1);
INSERT INTO public.post_like (id, local_date, friend_id, post_id) VALUES (1, '2020-01-26', 1, 1);