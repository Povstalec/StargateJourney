package net.povstalec.sgjourney.common.sgjourney;

import net.minecraft.SharedConstants;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.level.Level;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SpaceLocationTest
{
    private static final String MOST_SPECIFIC_PREFIX = "minecraft:overworld";

    private static void generateSpaceLocationTemplates(boolean reversed)
    {
        SpaceLocation.clear();
        List<ResourceKey<Galaxy>> galaxies = List.of();
        Comparator<SpaceLocation.TemplateInfo> comparator = Comparator.comparing(SpaceLocation.TemplateInfo::prefix);
        if (reversed)
        {
            comparator = comparator.reversed();
        }
        Stream.of(
                        new SpaceLocation.TemplateInfo("sgjourney:abydos", false, galaxies),
                        new SpaceLocation.TemplateInfo("minecraft:ov", false, galaxies),
                        new SpaceLocation.TemplateInfo("", false, galaxies),
                        new SpaceLocation.TemplateInfo("minecraft:", false, galaxies),
                        new SpaceLocation.TemplateInfo(MOST_SPECIFIC_PREFIX, false, galaxies),
                        new SpaceLocation.TemplateInfo("minecraft:over", false, galaxies),
                        new SpaceLocation.TemplateInfo("minecraft:the_end", false, galaxies)
                )
                .sorted(comparator)
                .map(info -> new SpaceLocation(info, false, 0f, false, false, null, null, null, false))
                .forEach(SpaceLocation::registerTemplate);
    }

    @BeforeAll
    static void setupMinecraft()
    {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void findSpaceLocationTemplateReturnsMostSpecificTemplate(boolean reversed)
    {
        generateSpaceLocationTemplates(reversed);

        final ResourceKey<Level> dimension = Level.OVERWORLD;
        final SpaceLocation template = SpaceLocation.findSpaceLocationTemplate(dimension);

        assertNotNull(template);
        assertNotNull(template.getTemplateInfo());
        assertEquals(MOST_SPECIFIC_PREFIX, template.getTemplateInfo().prefix());
    }
}
